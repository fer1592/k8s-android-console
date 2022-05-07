package com.fer1592.k8s_android_console.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Cluster")
data class Cluster(
    @PrimaryKey(autoGenerate = true)
    var clusterId: Long = 0L,
    var clusterName: String = "",
    var clusterAddress: String = "",
    var clusterPort: Int = 8443,
    var clusterAuthenticationMethod: String = "",
    var clusterBearerToken: String = "",
    @Ignore var validName: Boolean = true,
    @Ignore var validClusterAddress: Boolean = true,
    @Ignore var validClusterPort: Boolean = true,
    @Ignore var validClusterBearerToken: Boolean = true
) {
    // Public method to return if the cluster is valid or not, and to set the flags indicating which values are not valid
    fun isValid(): Boolean {
        var valid = true

        // Validate cluster Name
        if (this.clusterName.isEmpty()) {
            valid = false
            this.validName = false
        } else this.validName = true

        // Validate Cluster Address
        val addressRegex = "(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$)|(^[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b\$)".toRegex()
        if (!addressRegex.matches(this.clusterAddress)) {
            valid = false
            this.validClusterAddress = false
        } else this.validClusterAddress = true

        // Validate Cluster Port
        if (this.clusterPort !in 1..49151) {
            valid = false
            this.validClusterPort = false
        } else this.validClusterPort = true

        // Validate Auth Methods
        when (this.clusterAuthenticationMethod) {
            "Bearer Token" -> {
                if (this.clusterBearerToken.isEmpty()) {
                    valid = false
                    this.validClusterBearerToken = false
                } else this.validClusterBearerToken = true
            }
            else -> {
                valid = false
                this.validClusterBearerToken = false
            }
        }
        return valid
    }
}
