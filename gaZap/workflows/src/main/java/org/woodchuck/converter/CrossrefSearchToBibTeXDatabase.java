package org.woodchuck.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.StringValue;
import org.woodchuck.dtos.CrossrefSearchResponse;

public final class CrossrefSearchToBibTeXDatabase {

    private static final Key TYPE_MISC = new Key("misc");
    private static final Key KEY_JOURNAL = new Key("journal");
    private static final Key KEY_DOI = new Key("doi");
    private static final Key KEY_VOLUME = new Key("volume");
    private static final Key KEY_NUMBER = new Key("number");
    private static final Key KEY_PAGES = new Key("pages");
    private static final Key KEY_URL = new Key("url");

    private CrossrefSearchToBibTeXDatabase() {
    }

    public static BibTeXDatabase fromSearchResponse(CrossrefSearchResponse searchResponse) {
        BibTeXDatabase database = new BibTeXDatabase();

        if (searchResponse == null || searchResponse.message() == null || searchResponse.message().items() == null) {
            return database;
        }

        List<CrossrefSearchResponse.WorkItem> items = searchResponse.message().items();
        Set<String> usedKeys = new HashSet<>();
        int fallbackIndex = 1;

        for (CrossrefSearchResponse.WorkItem item : items) {
            if (item == null) {
                continue;
            }

            Key entryType = resolveType(item.type());
            String entryKey = createEntryKey(item, fallbackIndex++, usedKeys);
            BibTeXEntry entry = new BibTeXEntry(entryType, new Key(entryKey));

            addField(entry, BibTeXEntry.KEY_TITLE, firstNonBlank(item.getFirstTitle(), "Unknown Title"));
            addField(entry, KEY_JOURNAL, item.getFirstJournal());
            addField(entry, BibTeXEntry.KEY_AUTHOR, formatAuthors(item.authors()));
            addField(entry, BibTeXEntry.KEY_YEAR, item.issued() != null ? item.issued().getYear() : null);
            addField(entry, KEY_DOI, item.doi());
            addField(entry, KEY_VOLUME, item.volume());
            addField(entry, KEY_NUMBER, item.issue());
            addField(entry, KEY_PAGES, item.page());

            String doi = safe(item.doi());
            if (!doi.isBlank()) {
                addField(entry, KEY_URL, "https://doi.org/" + doi);
            }

            database.addObject(entry);
        }

        return database;
    }

    private static Key resolveType(String type) {
        String normalized = safe(type).toLowerCase();
        if (normalized.contains("book")) {
            return BibTeXEntry.TYPE_BOOK;
        }
        if (normalized.contains("article") || normalized.contains("journal")) {
            return BibTeXEntry.TYPE_ARTICLE;
        }
        return TYPE_MISC;
    }

    private static String createEntryKey(CrossrefSearchResponse.WorkItem item, int index, Set<String> usedKeys) {
        String doiKey = sanitizeKey(item.doi());
        String base = !doiKey.isBlank() ? doiKey : "ref" + index;
        String candidate = base;
        int suffix = 1;

        while (usedKeys.contains(candidate)) {
            candidate = base + suffix;
            suffix++;
        }

        usedKeys.add(candidate);
        return candidate;
    }

    private static void addField(BibTeXEntry entry, Key key, String value) {
        String normalized = safe(value).trim();
        if (!normalized.isEmpty()) {
            entry.addField(key, new StringValue(normalized, StringValue.Style.QUOTED));
        }
    }

    private static String formatAuthors(List<CrossrefSearchResponse.Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (CrossrefSearchResponse.Author author : authors) {
            if (author == null) {
                continue;
            }

            String family = safe(author.family()).trim();
            String given = safe(author.given()).trim();
            if (family.isEmpty() && given.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(" and ");
            }

            if (!family.isEmpty() && !given.isEmpty()) {
                result.append(family).append(", ").append(given);
            } else {
                result.append(!family.isEmpty() ? family : given);
            }
        }

        return result.toString();
    }

    private static String sanitizeKey(String raw) {
        return safe(raw).replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private static String firstNonBlank(String primary, String fallback) {
        String candidate = safe(primary).trim();
        return candidate.isEmpty() ? fallback : candidate;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
