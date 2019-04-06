package me.kbrewster.mojangapi.stats;

public enum MetricKeys {

    ITEM_MINECRAFT("item_sold_minecraft"),
    ITEM_COBOLT("item_sold_cobalt"),
    ITEM_SCROLLS("item_sold_scrolls"),
    PREPAID_MINECRAFT("prepaid_card_redeemed_minecraft");

    private String key;

    MetricKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
