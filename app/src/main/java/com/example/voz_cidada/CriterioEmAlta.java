package com.example.voz_cidada;

public class CriterioEmAlta {

    public static final int PESO_CURTIDA = 1;
    public static final int PESO_COMENTARIO = 2;
    public static final long JANELA_HORAS = 168; // 7 dias
    private static final double TOLERANCIA_RELOGIO_HORAS = 5.0 / 60.0; // 5 minutos
    private static final double GRAVIDADE = 1.5;
    private static final double MILIS_POR_HORA = 3600000.0;

    private CriterioEmAlta() {
        // Classe utilitária, não instanciar
    }

    // Entra na janela se tem até 7 dias, aceitando só uma pequena diferença de relógio
    public static boolean estaNaJanela(long criadoEmMillis, long agoraMillis) {
        double horas = horasDesde(criadoEmMillis, agoraMillis);
        return horas >= -TOLERANCIA_RELOGIO_HORAS && horas <= JANELA_HORAS;
    }

    // Fora da janela (muito antigo ou data no futuro) a pontuação é zero
    public static double calcularScore(long curtidas, long comentarios,
                                       long criadoEmMillis, long agoraMillis) {
        if (!estaNaJanela(criadoEmMillis, agoraMillis)) {
            return 0.0;
        }
        double interacoes = (curtidas * PESO_CURTIDA) + (comentarios * PESO_COMENTARIO);
        double horas = Math.max(0, horasDesde(criadoEmMillis, agoraMillis));
        return interacoes / Math.pow(horas + 2, GRAVIDADE);
    }

    private static double horasDesde(long criadoEmMillis, long agoraMillis) {
        return (agoraMillis - criadoEmMillis) / MILIS_POR_HORA;
    }
}
