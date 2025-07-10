package pw.coding.util

object ApiResponseMessages {
    const val USER_ALREADY_EXISTS = "A User with this email already exists."
    const val USER_NOT_FOUND = "Sorry couldn't found User"
    const val INVALID_CREDENTIALS = "Oops that  is not correct, please try again, "
    const val FIELDS_BLANK = "Field cannot be empty."
    const val COMMENT_TOO_LONG = "The comment length is exceed ${Constants.MAX_COMMENT_LENGTH} characters"
}