package com.testtask.myrecipes.data.storage

enum class TableConstance {
    KEY_ID { override fun value(): String { return "_id" }},
    KEY_ITEM {override fun value(): String { return "item" }},
    KEY_NAME { override fun value(): String { return "name" }},
    KEY_DESCRIPTION { override fun value(): String { return "description" }},
    KEY_HEADLINE { override fun value(): String { return "headline" }},
    KEY_DIFFICULTY { override fun value(): String { return "difficulty" }},
    KEY_CALORIES { override fun value(): String { return "calories" }},
    KEY_FATS { override fun value(): String { return "fats" }},
    KEY_PROTEINS { override fun value(): String { return "proteins" }},
    KEY_CARBOS { override fun value(): String { return "carbos" }},
    KEY_TIME { override fun value(): String { return "time" }},
    KEY_IMAGE_LINK_FULL { override fun value(): String { return "imageLink" }},
    KEY_IMAGE_STORAGE_FULL { override fun value(): String { return "imageAddress" }},
    KEY_IMAGE_LINK_PRE { override fun value(): String { return "preimageLink" }},
    KEY_IMAGE_STORAGE_PRE { override fun value(): String { return "preimageAddress" }};

    abstract fun value(): String
}