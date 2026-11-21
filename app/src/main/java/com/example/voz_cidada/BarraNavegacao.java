package com.example.voz_cidada;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

/**
 * Configura a barra de navegação inferior (view_bottom_nav.xml) de qualquer tela.
 * Feed e Em Alta ainda não existem: mostram um Toast "Em breve".
 */
public final class BarraNavegacao {

    public static final int FEED = 0;
    public static final int EM_ALTA = 1;
    public static final int DENUNCIAR = 2;
    public static final int PERFIL = 3;

    private BarraNavegacao() {
    }

    public static void configurar(Activity activity, int selecionado) {
        pintar(activity, R.id.navFeedIcone, R.id.navFeedTexto, selecionado == FEED);
        pintar(activity, R.id.navEmAltaIcone, R.id.navEmAltaTexto, selecionado == EM_ALTA);
        pintar(activity, R.id.navPerfilIcone, R.id.navPerfilTexto, selecionado == PERFIL);

        TextView denunciar = activity.findViewById(R.id.navDenunciarTexto);
        denunciar.setTextColor(ContextCompat.getColor(activity,
                selecionado == DENUNCIAR ? R.color.vc_azul : R.color.vc_texto_suave));

        activity.findViewById(R.id.navFeed).setOnClickListener(v -> emBreve(activity));
        activity.findViewById(R.id.navEmAlta).setOnClickListener(v -> emBreve(activity));
        activity.findViewById(R.id.navDenunciar).setOnClickListener(v ->
                abrir(activity, DenunciaActivity.class, selecionado == DENUNCIAR));
        activity.findViewById(R.id.navPerfil).setOnClickListener(v ->
                abrir(activity, PerfilActivity.class, selecionado == PERFIL));
    }

    private static void pintar(Activity activity, int idIcone, int idTexto, boolean ativo) {
        int cor = ContextCompat.getColor(activity, ativo ? R.color.vc_azul : R.color.vc_texto_suave);
        ((ImageView) activity.findViewById(idIcone)).setColorFilter(cor);
        ((TextView) activity.findViewById(idTexto)).setTextColor(cor);
    }

    private static void emBreve(Activity activity) {
        Toast.makeText(activity, R.string.nav_em_breve, Toast.LENGTH_SHORT).show();
    }

    private static void abrir(Activity activity, Class<? extends Activity> destino, boolean jaEstaAqui) {
        if (jaEstaAqui) {
            return;
        }
        Intent intent = new Intent(activity, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.finish();
    }
}
