package pw.coding.data.util

sealed class ParentType(val type: Int) {
    object Post: ParentType(0)
    object Comment: ParentType(1)

    companion object{
        fun fromType(type: Int): ParentType{
            return when(type){
                0 -> Post
                1 -> Comment
                else -> Comment
            }
        }
    }
}