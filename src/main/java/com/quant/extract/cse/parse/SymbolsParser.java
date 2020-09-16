package com.quant.extract.cse.parse;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class SymbolsParser {
    public List<String> parseSymbols(final byte[] listedCompanies) {
        String jsonStr = new String(listedCompanies, Charsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();

        List<String> symbols = new ArrayList<>();
        for (JsonElement element : jsonObject.get("reqByMarketcap").getAsJsonArray()) {
            String symbol = element.getAsJsonObject().get("symbol").getAsString();
            symbols.add(symbol);
        }

        return symbols;
    }
}
