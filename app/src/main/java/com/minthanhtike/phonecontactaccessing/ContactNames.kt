package com.minthanhtike.phonecontactaccessing

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ContactNamesGrid(
    contactArray: SnapshotStateList<ContactDetail>,
    onClick: (String) -> Unit
) {
    LazyColumn {
        items(contactArray) {
            Row {
                it.displayName?.let {
                    Text(text = it, modifier = Modifier.padding(30.dp))
                }
                val phLabel = it.phoneNumber.keys
                val emailLabel = it.email.keys
                phLabel.onEach { phType ->
                    it.phoneNumber[phType]?.let { phNo ->
                        Text(
                            text = phNo,
                            modifier = Modifier.padding(30.dp)
                        )
                    }
                }
                emailLabel.onEach { emailType ->
                    it.email[emailType]?.let { email ->
                        Text(
                            text = email,
                            modifier = Modifier.padding(30.dp)
                        )
                    }
                }

            }

        }
    }
}