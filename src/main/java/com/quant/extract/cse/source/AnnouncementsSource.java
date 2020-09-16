package com.quant.extract.cse.source;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.quant.extract.api.SourceException;
import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.CSESource;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsSource extends CSESource {
    final String symbol;

    public AnnouncementsSource(final CSEHttpReader httpReader, final String symbol) {
        super(httpReader);
        this.symbol = symbol;
    }


    @Override
    public List<DataFile> get() throws SourceException {
        List<DataFile> result = new ArrayList<>();

        final String body = "symbol=" + URLEncoder.encode(symbol, Charsets.UTF_8);
        byte[] announcements = getHttpReader().post("api/getAnnouncementByCompany", "application/x-www-form-urlencoded", body);

        String jsonStr = new String(announcements, Charsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        for (final JsonElement jsonElement : jsonObject.get("reqCompanyAnnouncement").getAsJsonArray()) {
            // Get announcement
            String announcementId = jsonElement.getAsJsonObject().get("announcementId").getAsString();
            String str = getAnnouncement(announcementId);
            // Save Announcement
            JsonObject json = JsonParser.parseString(str).getAsJsonObject();
            if (json.has("reqBaseAnnouncement")) {
                result.addAll(processNewAnnouncement(announcementId, json));
            } else {
                result.addAll(processLegacyAnnouncement(announcementId, json));
            }
        }
        return result;
    }

    private String getAnnouncement(final String announcementId) {
        String announcement = fetchAnnouncement(announcementId, "api/getAnnouncementById");
        if (announcement == null || announcement.isEmpty()) {
            announcement = fetchAnnouncement(announcementId, "api/getGeneralAnnouncementById");
            if (announcement == null || announcement.isEmpty()) {
                announcement = fetchAnnouncement(announcementId, "api/announcementById");
                if (announcement == null || announcement.isEmpty()) {
                    announcement = fetchAnnouncementLegacy(announcementId, "api/announcementById");
                }
            }
        }
        return announcement;
    }

    private String fetchAnnouncement(final String announcementId, final String url) {
        final String reqBody = "announcementId=" + URLEncoder.encode(announcementId, Charsets.UTF_8);
        byte[] announcement = getHttpReader().post(url, "application/x-www-form-urlencoded", reqBody);
        return new String(announcement, Charsets.UTF_8);
    }

    private String fetchAnnouncementLegacy(final String announcementId, final String url) {
        final String reqBody = "id=" + URLEncoder.encode(announcementId, Charsets.UTF_8);
        byte[] announcement = getHttpReader().post(url, "application/x-www-form-urlencoded", reqBody);
        return new String(announcement, Charsets.UTF_8);
    }

    private List<DataFile> processNewAnnouncement(final String announcementId, final JsonObject announcement) {
        List<DataFile> result = new ArrayList<>();
        String baseAnnouncement = announcement.get("reqBaseAnnouncement").getAsJsonObject().toString();
        result.add(new DataFile(announcementId + "/base", DataFile.FileType.JSON, baseAnnouncement.getBytes()));

        JsonArray docs = announcement.get("reqAnnouncementDocs").getAsJsonArray();
        for (JsonElement doc : docs) {
            JsonObject obj = doc.getAsJsonObject();
            final String fileName = obj.get("fileName").getAsString();
            final String url = obj.get("fileUrl").getAsString();
            final String contentType = obj.get("contentType").getAsString();
            byte[] file = getHttpReader().post(URLEncoder.encode(url, Charsets.UTF_8), contentType);

            result.add(new DataFile(announcementId + "/" + fileName, DataFile.FileType.PDF, file));

        }

        return result;
    }

    private List<DataFile> processLegacyAnnouncement(final String announcementId, final JsonObject announcement) {
        List<DataFile> result = new ArrayList<>();

        // Save base announcement
        JsonObject baseAnnouncement = announcement.get("infoAnnouncement").getAsJsonObject();
        result.add(new DataFile(announcementId + "/base", DataFile.FileType.JSON, baseAnnouncement.toString().getBytes()));

        // Save pdf file
        final String fileName = baseAnnouncement.get("title").getAsString();
        final JsonElement path = baseAnnouncement.get("filePath");
        if (path != null && !(path instanceof JsonNull)) {
            final String url = "cmt/" + path.getAsString();
            byte[] file = getHttpReader().post(URLEncoder.encode(url, Charsets.UTF_8), "application/pdf");
            result.add(new DataFile(announcementId + "/" + fileName, DataFile.FileType.PDF, file));
        }

        return result;
    }
}
