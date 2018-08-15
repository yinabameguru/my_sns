package com.jza.utils;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOW = "BIZ_FOLLOW";
    private static String BIZ_FOLLOWED = "BIZ_FOLLOWED";

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowKey(int entityType, int curUserId) {
        return BIZ_FOLLOW + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(curUserId);
    }

    public static String getFollowedKey(int entityType, int entityId) {
        return BIZ_FOLLOWED + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
