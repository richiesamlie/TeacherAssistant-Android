package com.localattendance.teacherassistant.ui.screen

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var showServerConfig by remember { mutableStateOf(false) }
    var serverUrl by remember {
        mutableStateOf(
            prefs.getString("server_url", ApiClient.DEFAULT_BASE_URL)
                ?: ApiClient.DEFAULT_BASE_URL
        )
    }
    var serverUrlSaved by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Teacher Assistant",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to continue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.clearError()
                            viewModel.login(username, password)
                        },
                        enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Default: admin / teacher123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Dns,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Server: ${serverUrl.take(35)}${if (serverUrl.length > 35) "..." else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showServerConfig = !showServerConfig }) {
                            Icon(
                                if (showServerConfig) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle server config"
                            )
                        }
                    }

                    AnimatedVisibility(visible = showServerConfig) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Enter your backend server URL (must end with /api/)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = serverUrl,
                                onValueChange = {
                                    serverUrl = it
                                    serverUrlSaved = false
                                },
                                label = { Text("Server URL") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Uri
                                ),
                                isError = serverUrl.isNotBlank() && !serverUrl.endsWith("/api/"),
                                supportingText = {
                                    if (serverUrl.isNotBlank() && !serverUrl.endsWith("/api/")) {
                                        Text("URL must end with /api/", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        serverUrl = "http://10.0.2.2:3000/api/"
                                        prefs.edit().putString("server_url", serverUrl).apply()
                                        ApiClient.clearInstance()
                                        serverUrlSaved = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Emulator")
                                }
                                FilledTonalButton(
                                    onClick = {
                                        serverUrl = "http://192.168.1.100:3000/api/"
                                        prefs.edit().putString("server_url", serverUrl).apply()
                                        ApiClient.clearInstance()
                                        serverUrlSaved = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Network")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (serverUrl.endsWith("/api/")) {
                                            prefs.edit().putString("server_url", serverUrl).apply()
                                            ApiClient.clearInstance()
                                            serverUrlSaved = true
                                        }
                                    },
                                    enabled = serverUrl.endsWith("/api/") && serverUrl.isNotBlank(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (serverUrlSaved) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Saved")
                                    } else {
                                        Icon(Icons.Default.Save, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Save URL")
                                    }
                                }
                                OutlinedButton(
                                    onClick = {
                                        serverUrl = ApiClient.DEFAULT_BASE_URL
                                        prefs.edit().putString("server_url", serverUrl).apply()
                                        ApiClient.clearInstance()
                                        serverUrlSaved = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reset")
                                }
                            }

                            if (serverUrlSaved) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✓ Server URL saved. Changes apply on next login.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
