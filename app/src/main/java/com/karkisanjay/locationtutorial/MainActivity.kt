package com.karkisanjay.locationtutorial

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.karkisanjay.locationtutorial.ui.theme.LocationTutorialTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val currentLocation = remember {
                mutableStateOf("")
            }

            val locationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if(isGranted){

                    scope.launch {
                        try {
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
                                fusedLocationProviderClient.lastLocation
                                    .addOnSuccessListener { location->
                                        location?.let {
                                            currentLocation.value = "${location.latitude}, ${location.longitude}"
                                        }

                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(context,"Failed to get location", Toast.LENGTH_SHORT).show()
                                    }
                            }



                        } catch (e: Exception){
                            e.printStackTrace()
                        }


                    }


                }else{
                    Toast.makeText(context,"Permission Denied", Toast.LENGTH_SHORT).show()
                }

            }

            LocationTutorialTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(text = "Location Tutorial") })
                    }) { innerPadding ->

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center){

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(text = currentLocation.value)

                            Button(onClick = {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }) {
                                Text(text = "Get Location")
                            }


                        }


                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LocationTutorialTheme {
        Greeting("Android")
    }
}