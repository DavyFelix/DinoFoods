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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import java.util.Locale

class AddressActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editEndereco: TextInputEditText
    private lateinit var btnLocalizacaoAtual: MaterialButton
    private lateinit var map: MapView // Adicionado para o Osmdroid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. CONFIGURAÇÃO OSMDROID (Deve vir antes do setContentView)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_addresses)

        // 2. INICIALIZAÇÃO DAS VIEWS
        editEndereco = findViewById(R.id.editEndereco)
        btnLocalizacaoAtual = findViewById(R.id.btnGetLocation)
        map = findViewById(R.id.mapView) // Certifique-se que o ID no XML é mapView

        // Configurações do Mapa
        map.setMultiTouchControls(true)
        // Opcional: Filtro Dark para o DinoFoods
        map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnLocalizacaoAtual.setOnClickListener {
            obterLocalizacao()
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
                Toast.makeText(this, "Ligue o GPS para obter a localização.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarMapa(lat: Double, lon: Double) {
        val ponto = GeoPoint(lat, lon)
        map.controller.setZoom(18.0)
        map.controller.setCenter(ponto)

        val marcador = Marker(map)
        marcador.position = ponto
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = "Dino Entrega Aqui!"

        map.overlays.clear()
        map.overlays.add(marcador)
        map.invalidate() // Força o mapa a redesenhar
    }

    private fun converterCoordenadasEmEndereco(lat: Double, long: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            // Usando a nova API do Geocoder para evitar travamentos na Main Thread (API 33+)
            val enderecos = geocoder.getFromLocation(lat, long, 1)
            if (!enderecos.isNullOrEmpty()) {
                val e = enderecos[0]
                val enderecoCompleto = "${e.thoroughfare ?: ""}, ${e.subThoroughfare ?: ""} - ${e.subLocality ?: ""}"

                editEndereco.setText(enderecoCompleto)
                salvarEnderecoNoAppwrite(enderecoCompleto)
            }
        } catch (e: Exception) {
            Log.e("GEO", "Erro ao converter: ${e.message}")
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
                Toast.makeText(this@AddressActivity, "Endereço atualizado!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Appwrite", "Erro: ${e.message}")
            }
        }
    }

    // CICLO DE VIDA OBRIGATÓRIO PARA OSMDROID
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}