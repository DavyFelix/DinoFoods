package br.davyfelix.dinofoods.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import java.util.Locale

class AddressActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editEndereco: TextInputEditText
    private lateinit var layoutEndereco: TextInputLayout
    private lateinit var btnLocalizacaoAtual: MaterialButton
    private lateinit var btnSalvar: MaterialButton
    private lateinit var btnVoltar: MaterialButton
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração obrigatória do Osmdroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_address)

        // Inicialização das Views
        editEndereco = findViewById(R.id.editEndereco)
        layoutEndereco = findViewById(R.id.layoutEndereco)
        btnLocalizacaoAtual = findViewById(R.id.btnGetLocation)
        btnSalvar = findViewById(R.id.btnSalvarEndereco)
        btnVoltar = findViewById(R.id.btnVoltar)
        map = findViewById(R.id.mapView)

        // Configurações do Mapa
        map.setMultiTouchControls(true)
        map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS) // Modo Dark

        configurarMapaInicial() // Centraliza no Brasil
        configurarCliqueNoMapa() // Permite marcar tocando

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cliques dos Botões
        btnLocalizacaoAtual.setOnClickListener { obterLocalizacao() }

        btnVoltar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val endereco = editEndereco.text.toString()
            if (endereco.isNotEmpty()) {
                salvarEnderecoNoAppwrite(endereco)
            } else {
                Toast.makeText(this, getString(R.string.selecione_endereco), Toast.LENGTH_SHORT).show()
            }
        }

        // Clique no ícone de Lupa (EndIcon) do TextInputLayout
        layoutEndereco.setEndIconOnClickListener {
            val texto = editEndereco.text.toString()
            if (texto.isNotEmpty()) {
                buscarEnderecoPorTexto(texto)
            } else {
                Toast.makeText(this, getString(R.string.digite_endereco), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarMapaInicial() {
        // Coordenadas centrais do Brasil
        val brasilia = GeoPoint(-15.793889, -47.882778)
        map.controller.setZoom(4.0)
        map.controller.setCenter(brasilia)
    }

    private fun configurarCliqueNoMapa() {
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                atualizarMapa(p.latitude, p.longitude)
                converterCoordenadasEmEndereco(p.latitude, p.longitude)
                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        }
        map.overlays.add(MapEventsOverlay(receiver))
    }

    private fun buscarEnderecoPorTexto(nomeEndereco: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resultados = geocoder.getFromLocationName(nomeEndereco, 1)
                if (!resultados.isNullOrEmpty()) {
                    val local = resultados[0]
                    withContext(Dispatchers.Main) {
                        atualizarMapa(local.latitude, local.longitude)
                        val formatado = "${local.thoroughfare ?: ""}, ${local.subThoroughfare ?: ""} - ${local.subLocality ?: ""}"
                        editEndereco.setText(formatado)
                    }
                }
            } catch (e: Exception) {
                Log.e("GEO", getString(R.string.erro_busca, e.message))
            }
        }
    }

    private fun obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                atualizarMapa(location.latitude, location.longitude)
                converterCoordenadasEmEndereco(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, getString(R.string.ligar_gps), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarMapa(lat: Double, lon: Double) {
        val ponto = GeoPoint(lat, lon)
        map.controller.animateTo(ponto)
        map.controller.setZoom(18.0)

        val marcador = Marker(map)
        marcador.position = ponto
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = getString(R.string.dino_entrega)

        // Limpa marcadores anteriores
        val markers = map.overlays.filterIsInstance<Marker>()
        map.overlays.removeAll(markers)

        map.overlays.add(marcador)
        map.invalidate()
    }

    private fun converterCoordenadasEmEndereco(lat: Double, long: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val enderecos = geocoder.getFromLocation(lat, long, 1)
                if (!enderecos.isNullOrEmpty()) {
                    val e = enderecos[0]
                    val enderecoCompleto = "${e.thoroughfare ?: ""}, ${e.subThoroughfare ?: ""} - ${e.subLocality ?: ""}"
                    withContext(Dispatchers.Main) {
                        editEndereco.setText(enderecoCompleto)
                    }
                }
            } catch (e: Exception) {
                Log.e("GEO", "Erro ao converter: ${e.message}")
            }
        }
    }

    private fun salvarEnderecoNoAppwrite(endereco: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        lifecycleScope.launch {
            try {
                AppwriteService.getDatabase().updateDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_USUARIOS,
                    documentId = user.uid,
                    data = mapOf("endereco" to endereco)
                )
                Toast.makeText(this@AddressActivity,
                    getString(R.string.endereco_salvo), Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("Appwrite", "Erro ao salvar: ${e.message}")
                Toast.makeText(this@AddressActivity,
                    getString(R.string.erro_endereco), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}