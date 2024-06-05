package com.muzafferatmaca.dragdropcompose

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.os.TestLooperManager
import android.provider.Settings.Global.getString
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.muzafferatmaca.dragdropcompose.ui.theme.DragDropComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragDropApp()
        }
    }
}

@Composable
fun DragDropApp() {
    DragDropComposeTheme {
        Column(
            modifier = Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DragImage(url = stringResource(id = R.string.source_url))
            DropTargetImage(url = stringResource(id = R.string.target_url))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DragImage(url: String) {
    GlideImage(model = url,
        contentDescription = "Dragged Image",
        modifier = Modifier.dragAndDropSource {
            detectTapGestures(
                onLongPress = {
                    startTransfer(
                        DragAndDropTransferData(
                            ClipData.newPlainText("image uri", url)
                        )
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DropTargetImage(url: String) {
    val urlState = remember { mutableStateOf(url) }

    var tintColor by remember {
        mutableStateOf(Color(0xffE5E4E2))
    }
    val dndTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val draggedData = event.toAndroidDragEvent()
                    .clipData.getItemAt(0).text
                urlState.value = draggedData.toString()
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                tintColor = Color(0xff00ff00)
            }

            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                tintColor = Color(0xffE5E4E2)
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                tintColor = Color(0xffE5E4E2)
            }

        }
    }
    GlideImage(
        model = urlState.value,
        contentDescription = "Dropped Image",
        colorFilter = ColorFilter.tint(color = tintColor, blendMode = BlendMode.Modulate),
        modifier = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = {event ->
                event
                    .mimeTypes()
                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
            },
            target = dndTarget
        )
    )
}



