import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncCryptoApp extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;
   
    // Създаваме изпълнител с една единствена нишка.
    // Това гарантира, че задачите се изпълняват стриктно в реда, в който постъпват.
    private final ExecutorService processingQueue = Executors.newSingleThreadExecutor();

    public AsyncCryptoApp() {
        super("Асинхронно Криптиране");
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null); // Центриране на екрана

        // Ляв панел (Вход)
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        JScrollPane leftScroll = new JScrollPane(inputArea);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Вход (Пишете тук)"));

        // Десен панел (Изход - криптиран)
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setEditable(false); // Само за четене
        outputArea.setBackground(new Color(230, 230, 230));
        JScrollPane rightScroll = new JScrollPane(outputArea);
        rightScroll.setBorder(BorderFactory.createTitledBorder("Изход (Криптиран текст)"));

        // Разделител
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
        splitPane.setResizeWeight(0.5); // По равно място за двата панела

        add(splitPane);
    }

    private void setupListeners() {
        // DocumentListener следи за всяка промяна (вмъкване, триене, промяна)
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleEncryption();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleEncryption();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleEncryption();
            }
        });
    }

    // Метод за добавяне на задача в опашката
    private void scheduleEncryption() {
        // Взимаме текущия текст веднага (от UI нишката)
        String currentText = inputArea.getText();

        // Изпращаме задачата към фоновата нишка
        processingQueue.submit(() -> {
            try {
                // 1. Симулация на тежка работа (за да се види асинхронността)
                // Ако пишете много бързо, ще видите леко забавяне, но редът ще се запази.
                Thread.sleep(50);

                // 2. Същинско криптиране (в случая Base64 + отместване)
                String encryptedText = performEncryption(currentText);

                // 3. Връщане на резултата в UI нишката (Event Dispatch Thread)
                SwingUtilities.invokeLater(() -> {
                    // Проверка дали прозорецът все още е активен
                    if (outputArea != null) {
                        outputArea.setText(encryptedText);
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // Примерна логика за криптиране
    private String performEncryption(String text) {
        if (text.isEmpty()) return "";
       
        // Пример: Обръщане на стринга + Base64 енкодинг
        String reversed = new StringBuilder(text).reverse().toString();
        return Base64.getEncoder().encodeToString(reversed.getBytes());
    }

    public static void main(String[] args) {
        // Стартиране на приложението в UI нишката
        SwingUtilities.invokeLater(() -> {
            new AsyncCryptoApp().setVisible(true);
        });
    }
}
