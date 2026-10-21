package com.example.voz_cidada;

public class CriterioEmAlta {

    public static final int PESO_CURTIDA = 1;
    public static final int PESO_COMENTARIO = 2;
    public static final long JANELA_HORAS = 168; // 7 dias
    private static final double GRAVIDADE = 1.5;
    private static final double MILIS_POR_HORA = 3600000.0;

    private CriterioEmAlta() {
        // Classe utilitária, não instanciar
    }

    // Só posts dentro da janela de tempo entram no Em Alta
    public static boolean estaNaJanela(long criadoEmMillis, long agoraMillis) {
        return horasDesde(criadoEmMillis, agoraMillis) <= JANELA_HORAS;
    }

    // Quanto mais interação e mais recente, maior a pontuação
    public static double calcularScore(long curtidas, long comentarios,
                                       long criadoEmMillis, long agoraMillis) {
        double interacoes = (curtidas * PESO_CURTIDA) + (comentarios * PESO_COMENTARIO);
        double horas = horasDesde(criadoEmMillis, agoraMillis);
        return interacoes / Math.pow(horas + 2, GRAVIDADE);
    }

    private static double horasDesde(long criadoEmMillis, long agoraMillis) {
        double horas = (agoraMillis - criadoEmMillis) / MILIS_POR_HORA;
        return Math.max(0, horas); // evita valor negativo se o relógio estiver adiantado
    }
}
