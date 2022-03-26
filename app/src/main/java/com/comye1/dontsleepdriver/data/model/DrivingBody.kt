package com.comye1.dontsleepdriver.data.model

data class DrivingBody(
    val startTime: String,
    val endTime: String,
    val gpsData: Array<Location>,
    val gpsLevel: Array<Int>,
    val avgSleepLevel: Double,
    val totalTime: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrivingBody

        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (!gpsData.contentEquals(other.gpsData)) return false
        if (!gpsLevel.contentEquals(other.gpsLevel)) return false
        if (avgSleepLevel != other.avgSleepLevel) return false
        if (totalTime != other.totalTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + gpsData.contentHashCode()
        result = 31 * result + gpsLevel.contentHashCode()
        result = 31 * result + avgSleepLevel.hashCode()
        result = 31 * result + totalTime
        return result
    }
}
