package ca.mohawkcollege.petcupid.tools

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * ContentUtils
 * Convert uri to file path
 */
object ContentUtils {

    /**
     * Get file path from uri
     * @param context Context
     * @param uri Uri
     * @return String?
     */
    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val contentResolver = context.contentResolver

        if (isMediaDocument(uri)) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                /**
                 * Example:
                 * Uri -> content://com.android.providers.media.documents/document/video%3A118
                 * docId -> video:118
                 * split[0] -> video
                 * split[1] -> 118
                 * selection -> _id=?
                 * selectionArgs -> 118
                 * contentUri -> content://media/external/video/media
                 * projectionData -> _data
                 */
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                val fileType = split[0]
                var contentUri: Uri? = null
                var projectionData: String? = null
                when (fileType) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        projectionData = MediaStore.Images.Media.DATA
                    }

                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        projectionData = MediaStore.Video.Media.DATA
                    }

                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        projectionData = MediaStore.Audio.Media.DATA
                    }
                }

                filePath = getFilePathFromMediaStore(
                    contentResolver,
                    contentUri!!,
                    projectionData!!,
                    selection,
                    selectionArgs
                )
            } else if ("content" == uri.scheme) {
                filePath = getFilePathFromMediaStore(contentResolver, uri, "", null, null)
            } else if ("file" == uri.scheme) {
                filePath = uri.path
            }
        }

        return filePath
    }

    /**
     * Get file path from media store
     * @param contentResolver ContentResolver
     * @param uri Uri
     * @param projectionData String
     * @param selection String?
     * @param selectionArgs Array<String>?
     * @return String?
     */
    private fun getFilePathFromMediaStore(
        contentResolver: ContentResolver,
        uri: Uri,
        projectionData: String,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var filePath: String? = null
        val projection = arrayOf(projectionData)
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(projectionData)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }

    /**
     * Is media document
     * @param uri Uri
     * @return Boolean
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * Is downloads document
     * @param uri Uri
     * @return Boolean
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * Is external storage document
     * @param uri Uri
     * @return Boolean
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
}