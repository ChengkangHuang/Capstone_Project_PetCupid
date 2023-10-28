package ca.mohawkcollege.petcupid.tools

object ChatUtils {

    /**
     * Get the chat uid for the chat room.
     * @param thisUid The UID of the current user.
     * @param thatUid The UID of the other user.
     * @return The chat UID.
     */
    fun getChatUid(thisUid: String, thatUid: String): String {
        return if (thisUid > thatUid) thisUid + thatUid else thatUid + thisUid
    }
}