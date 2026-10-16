package calc.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Memoria {

    // Não precisa criar outra classe do tipo ENUM, é possível também criar numa classe já existente
    private enum TipoComando {
        ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, PONTO, INV_SINAL, VOLTAR, PORCENTAGEM, ELEV_QUADRADO, RAIZ_QUADRADA;
    }

    private static final Memoria instancia = new Memoria(); // ela vai controlar essa única instância dentro dela mesma

    private TipoComando ultimaOperacao = null;
    private boolean substituir = false;
    private boolean porcentagem = false;
    private String textoAtual = "";
    private String textoBuffer = "";

    private final List<MemoriaObservador> observadores = new ArrayList<>();

    private Memoria() {
//padrão Singleton (onde garante que uma classe só possua uma instância e que essa seja usada como ponto de acesso global
    }

    public static Memoria getInstancia() {
        return instancia;
    }

    public void adicionarObservador(MemoriaObservador observador){
        observadores.add(observador);
    }

    public String getTextoAtual() {
        return textoAtual.isEmpty() ? "0" : textoAtual;
    }

    public void processarComando(String comando) {
        // FIXME não consigo pôr 0.1, 0.25... quando o display está vazio (por exemplo quando abre a calc)
        // FIXME após uma operação do tipo 6 - 6 = 0, ao clicar em ponto, ele substitui por um ponto ao invés de "0.",
        //  tendo que apertar no zero primeiro e dps no ponto

        // FIXME fazer diminuir a fonte para caber o número no display

        TipoComando tipoComando = detectarTipoComando(comando);

        if(tipoComando == null) {
            return;
        } else if (tipoComando == TipoComando.ZERAR) {
            textoAtual = "";
            textoBuffer = "";
            substituir = false;
            ultimaOperacao = null;
        } else if (tipoComando == TipoComando.VOLTAR) {
            try {
                textoAtual = textoAtual.substring(0, textoAtual.length() - 1);
            } catch (Exception e) {
                // ignora quando apaga tudo, exibindo apenas 0 no display
            }
        } else if (tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.PONTO) {

            if (!textoAtual.isEmpty()) {
                getTextoAtual();
                textoAtual = substituir ? comando : textoAtual + comando;
                substituir = false;
            } else if (tipoComando == TipoComando.NUMERO){
                textoAtual = comando;
                substituir = false;
            }

        } else if (tipoComando == TipoComando.PORCENTAGEM) {
            try {
                textoAtual = "" + Double.parseDouble(textoAtual)/100;
                porcentagem = true;
            } catch (NumberFormatException e) {
                // Caso esteja vazio
            }

        } else if (tipoComando == TipoComando.ELEV_QUADRADO) {
            try {
                double elevarAoQuadrado = Math.pow(Double.parseDouble(textoAtual), 2);
                textoAtual = "" + elevarAoQuadrado;

                boolean inteiro = textoAtual.endsWith(".0");
                textoAtual = inteiro ? textoAtual.replace(".0", "") : textoAtual;
            } catch (NumberFormatException e) {
                // Caso esteja vazio
            }

        } else if (tipoComando == TipoComando.INV_SINAL) {

            try {
                if(Double.parseDouble(textoAtual) > 0) {
                    textoAtual = "-" + textoAtual;
                } else if  (Double.parseDouble(textoAtual) < 0){
                    textoAtual = textoAtual.substring(1);
                }
            } catch (NumberFormatException e) {
                // Caso apenas exiba 0 no display, não faz nada
            }

        } else if (tipoComando == TipoComando.RAIZ_QUADRADA) {
            try {
                double raizQuadrada = Math.sqrt(Double.parseDouble(textoAtual));
                textoAtual = "" + raizQuadrada;

                boolean inteiro = textoAtual.endsWith(".0");
                textoAtual = inteiro ? textoAtual.replace(".0", "") : textoAtual;
            } catch (NumberFormatException e) {
                // Caso esteja vazio
            }

        } else if (tipoComando == TipoComando.IGUAL) {
            try {
                substituir = true;
                double resultado;
                double entrada1 = Double.parseDouble(textoBuffer);
                double entrada2 = Double.parseDouble(textoAtual);

                switch(ultimaOperacao) {
                    case SOMA:
                        if (porcentagem) {
                            resultado = entrada1 + (entrada1 * entrada2);
                            porcentagem = false;
                        } else {
                            resultado = entrada1 + entrada2;
                        }
                        textoAtual = "" + resultado; break;
                    case SUB:
                        if (porcentagem) {
                            resultado = entrada1 - (entrada1 * entrada2);
                            porcentagem = false;
                        } else {
                            resultado = entrada1 - entrada2;
                        }
                        textoAtual = "" + resultado; break;
                    case MULT:
                        resultado = entrada1 * entrada2;
                        textoAtual = "" + resultado; break;
                    case DIV:
                        try {
                            resultado = entrada1 / entrada2;
                            textoAtual = "" + resultado;
                        } catch (Exception e) {
                            // Caso haja alguma divisão por zero
                        } break;
                }

                boolean inteiro = textoAtual.endsWith(".0");
                textoAtual = inteiro ? textoAtual.replace(".0", "") : textoAtual;
            } catch (Exception e) {
                // Ignora
            }

        } else { // vão sobrar apenas as operações básicas (soma, sub, mult e div)
            substituir = true;
            ultimaOperacao = tipoComando;
            textoBuffer = textoAtual;
        }

        observadores.forEach(observador -> observador.valorAlterado(getTextoAtual()));
    }

    private TipoComando detectarTipoComando(String comando) {

        if(textoAtual.isEmpty() && comando == "0") {
            return null;
        }

        try {
            Integer.parseInt(comando);
            return TipoComando.NUMERO;
        } catch (NumberFormatException e) {

            if("CE".equals(comando)) {
                textoAtual = "";
                return TipoComando.ZERAR;
            } else if ("←".equals(comando)) {
                return TipoComando.VOLTAR;
                
            } else if ("=".equals(comando)) {
                return TipoComando.IGUAL;

            } else if ("÷".equals(comando)) {
                return TipoComando.DIV;

            } else if ("×".equals(comando)) {
                return TipoComando.MULT;

            } else if ("-".equals(comando)) {
                return TipoComando.SUB;

            } else if ("+".equals(comando)) {
                return TipoComando.SOMA;

            } else if ("%".equals(comando)) {
                return TipoComando.PORCENTAGEM;

            } else if ("x²".equals(comando)) {
                return TipoComando.ELEV_QUADRADO;

            } else if ("²√x".equals(comando)) {
                return TipoComando.RAIZ_QUADRADA;

            } else if ("±".equals(comando)) {
                return TipoComando.INV_SINAL;

            } else if (".".equals(comando) && !textoAtual.contains(".")) {
                return TipoComando.PONTO;
            }
        }

        return null;
    }
}
