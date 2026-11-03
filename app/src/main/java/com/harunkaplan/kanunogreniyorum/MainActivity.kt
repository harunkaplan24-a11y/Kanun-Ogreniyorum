package com.harunkaplan.kanunogreniyorum

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val tone by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 80) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KanunScreen { tone.startTone(ToneGenerator.TONE_PROP_BEEP, 180) } }
    }
    override fun onDestroy() { tone.release(); super.onDestroy() }
}

@Composable
private fun KanunScreen(play: () -> Unit) {
    val notes = listOf("Do","Do♯","Re","Re♯","Mi","Fa","Fa♯","Sol","Sol♯","La","La♯","Si")
    val makams = listOf("Rast","Uşşak","Hicaz","Hüseyni","Nihavent","Kürdi","Segâh")
    var octave by remember { mutableIntStateOf(4) }
    var mandal by remember { mutableIntStateOf(0) }
    var makam by remember { mutableStateOf("Rast") }
    var tab by remember { mutableStateOf(0) }
    var tempo by remember { mutableFloatStateOf(80f) }
    var last by remember { mutableStateOf("Bir tel seç") }
    val brown = Color(0xFF704126)
    val cream = Color(0xFFFFF4DF)
    MaterialTheme(colorScheme = lightColorScheme(primary = brown, background = cream)) {
        Scaffold(
            topBar = { Column(Modifier.fillMaxWidth().background(brown).padding(18.dp)) {
                Text("KANUN", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Çal • Tanı • Öğren", color = Color(0xFFFFDFA3))
            } },
            bottomBar = { NavigationBar {
                listOf("Çal","Öğren","Ayarlar").forEachIndexed { i, label ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i },
                        icon = { Text(listOf("♫","♪","⚙")[i]) }, label = { Text(label) })
                }
            } },
            containerColor = cream
        ) { pad ->
            Column(Modifier.padding(pad).fillMaxSize().padding(14.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                when(tab) {
                    0 -> {
                        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Çalma alanı", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Oktav: $octave")
                                Spacer(Modifier.weight(1f))
                                OutlinedButton(onClick = { if(octave>2) octave-- }) { Text("−") }
                                OutlinedButton(onClick = { if(octave<6) octave++ }) { Text("+") }
                            }
                            Text("Teller • $last")
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                notes.forEach { note ->
                                    val dark = note.contains("♯")
                                    Box(Modifier.width(58.dp).height(142.dp).background(if(dark) Color(0xFF493B35) else Color.White, RoundedCornerShape(8.dp))
                                        .clickable { last = "$note$octave"; play() }.padding(6.dp),
                                        contentAlignment = Alignment.BottomCenter) {
                                        Text(note, color = if(dark) Color.White else brown, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } }
                        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Mandal / koma ayarı", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Seçim: ${mandal * 50} cent")
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(-2,-1,0,1,2).forEach { v ->
                                    FilterChip(selected = mandal==v, onClick = { mandal=v },
                                        label = { Text(if(v>0) "+$v" else "$v") })
                                }
                            }
                        } }
                        Card { Column(Modifier.padding(14.dp)) {
                            Text("Makam seçimi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            makams.forEach { m -> Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = makam==m, onClick = { makam=m })
                                Text(m)
                            } }
                        } }
                    }
                    1 -> {
                        Text("Kanun öğrenme", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Nota isimleri", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Do–Re–Mi–Fa–Sol–La–Si dizisi bir oktavı oluşturur. Diyez sesi yarım ses yükseltir; bemol yarım ses düşürür.")
                            Text("Kanunda mandallar, tellerin sesini küçük aralıklarla değiştirerek makamların karakteristik aralıklarını oluşturur.")
                        } }
                        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Çalışma önerisi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("1. Karar sesini tanı\n2. Makam dizisini yavaş tempoda çal\n3. Güçlü ve yarım karar seslerini dinle\n4. Kısa seyir kalıbını tekrar et")
                        } }
                    }
                    else -> {
                        Text("Çalışma ayarları", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Tempo: ${tempo.toInt()} BPM")
                        Slider(value = tempo, onValueChange = { tempo=it }, valueRange = 40f..180f)
                        Text("Bu test sürümünde nota tuşları sesli geri bildirim verir. Duyulan ses henüz gerçek kanun kaydı değildir; lisanslı kanun örnekleri ayrıca eklenmelidir.")
                        Text("Makam: $makam • Mandal: ${mandal * 50} cent")
                    }
                }
            }
        }
    }
}
