package com.itti.leadcapturing.model;

/**
 * Approval levels in workflow sequence
 * Order: PROCUREMENT → COO → CEO → FINANCE
 */
public enum ApprovalLevel {
    PROCUREMENT(1, "Procurement Team"),
    COO(2, "Chief Operating Officer"),
    CEO(3, "Chief Executive Officer"),
    FINANCE(4, "Finance Team");

    private final int order;
    private final String displayName;

    ApprovalLevel(int order, String displayName) {
        this.order = order;
        this.displayName = displayName;
    }

    public int getOrder() {
        return order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ApprovalLevel getByOrder(int order) {
        for (ApprovalLevel level : values()) {
            if (level.order == order) {
                return level;
            }
        }
        return null;
    }

    public ApprovalLevel getNext() {
        int nextOrder = this.order + 1;
        return getByOrder(nextOrder);
    }

    public boolean isFirstLevel() {
        return this.order == 1;
    }

    public boolean isLastLevel() {
        return this.order == 4;
    }
}