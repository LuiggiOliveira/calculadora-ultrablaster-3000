package calc.visao;

import calc.modelo.Memoria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Teclado extends JPanel implements ActionListener {

    private final Color CINZA_ESCURO= new Color(68, 68, 68);
    private final Color CINZA_CLARO= new Color(161, 161, 161);
    private final Color LARANJA = new Color(237, 164, 61);
    private final Color AZUL = new Color(0, 103, 192);

    public Teclado() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(layout);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        // Linha 1
        c.gridwidth = 2;
        adicionarBotao("CE", CINZA_ESCURO, c, 0, 0);
        adicionarBotao("←", CINZA_ESCURO, c, 2, 0);

        // Linha 2
        c.gridwidth = 1;
        adicionarBotao("%", LARANJA, c, 0, 1);
        adicionarBotao("x²", LARANJA, c, 1, 1);
        adicionarBotao("²√x", LARANJA, c, 2, 1);
        adicionarBotao("÷", LARANJA, c, 3, 1);

        // Linha 3
        adicionarBotao("7", CINZA_CLARO, c, 0, 2);
        adicionarBotao("8", CINZA_CLARO, c, 1, 2);
        adicionarBotao("9", CINZA_CLARO, c, 2, 2);
        adicionarBotao("×", LARANJA, c, 3, 2);

        // Linha 4
        adicionarBotao("4", CINZA_CLARO, c, 0, 3);
        adicionarBotao("5", CINZA_CLARO, c, 1, 3);
        adicionarBotao("6", CINZA_CLARO, c, 2, 3);
        adicionarBotao("-", LARANJA, c, 3, 3);

        // Linha 5
        adicionarBotao("1", CINZA_CLARO, c, 0, 4);
        adicionarBotao("2", CINZA_CLARO, c, 1, 4);
        adicionarBotao("3", CINZA_CLARO, c, 2, 4);
        adicionarBotao("+", LARANJA, c, 3, 4);

        // Linha 6
        adicionarBotao("±", CINZA_CLARO, c, 0, 5);
        adicionarBotao("0", CINZA_CLARO, c, 1, 5);
        adicionarBotao(".", CINZA_CLARO, c, 2, 5);
        adicionarBotao("=", AZUL, c, 3, 5);
    }

    private void adicionarBotao(String texto, Color cor, GridBagConstraints c, int x, int y) {
        c.gridx = x;
        c.gridy = y;
        Botao botao = new Botao(texto, cor);
        botao.addActionListener(this); // para cada botão está registrado o observador teclado
        add(botao, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton) {
            JButton botao = (JButton) e.getSource(); // getSource retorna Object, por isso o cast
            Memoria.getInstancia().processarComando(botao.getText());
        }
    }
}
