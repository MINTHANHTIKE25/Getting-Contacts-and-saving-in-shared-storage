package com.minthanhtike.phonecontactaccessing

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.minthanhtike.phonecontactaccessing.ui.theme.PhoneContactAccessingTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneContactAccessingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val mediaStoreManager = MediaStoreManager()
                    val coroutineScope = rememberCoroutineScope()
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources, R.drawable.activity_state_and_fragments_call_back
                    )
                    val contactManager = ContactManager(contentResolver = context.contentResolver)
                    val contactList: SnapshotStateList<ContactDetail> = remember { mutableStateListOf() }
                    Greeting(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            bitmap?.let {
                                mediaStoreManager.saveImageToMediaStore(
                                    context, displayName = "My Photos", bitmap = it
                                )
                            }
                        }
                    }, onContact = {
                        coroutineScope.launch {
                           contactManager.getContactDetail(context)?.let {
                               contactList.addAll(it)
                           }
                        }
                    })
                    contactList.let {
                        AnimatedVisibility(
                            visible = it.isNotEmpty(),
                            enter = fadeIn() + expandVertically { -6000 },
                            exit = fadeOut(
                                targetAlpha = 0.4f,
                                animationSpec = spring(stiffness = Spring.StiffnessHigh)
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ContactNamesGrid(contactArray = it, onClick = {})
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, onClick: () -> Unit, onContact: () -> Unit) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Save image to the share storage",
                modifier = modifier.clickable {
                    onClick()
                }
            )
            Divider(Modifier.padding(30.dp))
            Text(
                text = "See your profile on contract",
                modifier = Modifier.clickable {
                    onContact()
                }
            )


        }

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhoneContactAccessingTheme {
        Greeting(onClick = {}) {}
    }
}