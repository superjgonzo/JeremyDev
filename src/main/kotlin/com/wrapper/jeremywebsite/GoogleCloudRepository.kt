package com.wrapper.jeremywebsite

import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1beta1.SecretVersionName
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class GoogleCloudRepository {
  @Throws(IOException::class)
  fun accessSecretVersion(): String {
    // TODO(developer): Replace these variables before running the sample.
    val projectId = "mystic-column-275402"
    val secretId = "discord-token"
    val versionId = "1"
    return accessSecretVersion(projectId, secretId, versionId)
  }

  // Access the payload for the given secret version if one exists. The version
  // can be a version number as a string (e.g. "5") or an alias (e.g. "latest").
  @Throws(IOException::class)
  fun accessSecretVersion(projectId: String?, secretId: String?, versionId: String?): String {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    SecretManagerServiceClient.create().use { client ->
      val secretVersionName = SecretVersionName.of(projectId, secretId, versionId)

      // Access the secret version.
      val response = client.accessSecretVersion(secretVersionName)

      // Print the secret payload.
      //
      // WARNING: Do not print the secret in a production environment - this
      // snippet is showing how to access the secret material.
      return response.payload.data.toStringUtf8()

    }
  }
}