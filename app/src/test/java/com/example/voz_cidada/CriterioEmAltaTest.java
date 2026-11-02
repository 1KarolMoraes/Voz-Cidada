package com.example.voz_cidada;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CriterioEmAltaTest {

    private static final long HORA = 3600000L;
    private static final long AGORA = 1_000_000_000_000L;

    @Test
    public void comentarioValeMaisQueCurtida() {
        double soCurtida = CriterioEmAlta.calcularScore(1, 0, AGORA - HORA, AGORA);
        double soComentario = CriterioEmAlta.calcularScore(0, 1, AGORA - HORA, AGORA);
        assertTrue(soComentario > soCurtida);
    }

    @Test
    public void postSemInteracaoTemScoreZero() {
        assertEquals(0.0, CriterioEmAlta.calcularScore(0, 0, AGORA - HORA, AGORA), 0.0001);
    }

    @Test
    public void postMaisNovoComMesmaInteracaoTemScoreMaior() {
        double novo = CriterioEmAlta.calcularScore(5, 2, AGORA - HORA, AGORA);
        double antigo = CriterioEmAlta.calcularScore(5, 2, AGORA - 48 * HORA, AGORA);
        assertTrue(novo > antigo);
    }

    @Test
    public void postDentroDeSeteDiasEntraNaJanela() {
        assertTrue(CriterioEmAlta.estaNaJanela(AGORA - 24 * HORA, AGORA));
    }

    @Test
    public void postComMaisDeSeteDiasFicaForaDaJanela() {
        assertFalse(CriterioEmAlta.estaNaJanela(AGORA - 200 * HORA, AGORA));
    }

    @Test
    public void relogioAdiantadoNaoGeraScoreNegativo() {
        double score = CriterioEmAlta.calcularScore(3, 1, AGORA + HORA, AGORA);
        assertTrue(score >= 0);
    }
}
