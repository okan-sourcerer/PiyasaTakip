package com.example.piyasatakip

data class PiyasaBilgisi(var shortName: String, var fullName: String, var priceHistory: List<Double>,
                         var type: String,
                         var current: Double, var imagePath: String = "", var isFav: Boolean = false){

    /**
     * Empty constructor needed to initialize from the firebase.
     */
    constructor(): this("", "", listOf(), "", 0.0)

    override fun toString(): String {
        return "$shortName $fullName $type $current $priceHistory $imagePath"
    }
}
