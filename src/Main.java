
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * PENEVAL Cifrado clave privada - FASE 1
 * ----------------------------------------
 *
 * @author Rubén
 */
public class Main {

    private static File ficheroOrigen;
    private static File ficheroCifrado;
    private static File ficheroClave;
    private static String rutaFicheroOrigen;
    private static String rutaFicheroCifrado;
    private static String rutaFicheroClave;
    private static Ventana ventana;

    public static void main(String[] args) {
        //Creación y visualización de la interfaz gráfica.
        ventana = new Ventana();
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);

        //Asignación de las rutas de fichero por defecto
        rutaFicheroOrigen = "./src/ficheros/fichero_original.txt";
        rutaFicheroCifrado = "./src/ficheros/fichero_codificado.txt";
        rutaFicheroClave = "./src/ficheros/fichero_clave_privada.txt";
    }

    public static void realizarProceso() {
        SecretKey clave = null;
        if (asignarRutaFicheroClave() && asignarRutaFicheroCodificado()) {
            //Generación de la clave DES
            try {
                clave = generarClaveDES();
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("ERROR en la encriptación o generación de la clave DES: " + ex);
                JOptionPane.showMessageDialog(ventana, "ERROR en la encriptación o generación de la clave DES: " + ex, "ERROR", JOptionPane.ERROR);
            }
            //Encriptación y almaenamiento del fichero seleccionado
            try {
                encriptar(clave, new FileInputStream(ficheroOrigen), new FileOutputStream(ficheroCifrado));
            } catch (FileNotFoundException ex) {
                System.out.println("ERROR en el acceso al fichero: " + ex);
                JOptionPane.showMessageDialog(ventana, "ERROR en el acceso al fichero: " + ex, "ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | IOException ex) {
                System.out.println("ERROR en la encriptación del fichero: " + ex);
                JOptionPane.showMessageDialog(ventana, "ERROR en la encriptación del fichero: " + ex, "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            //Almacenamiento de la clave
            try {
                guardarClave(clave, new FileOutputStream(ficheroClave));
            } catch (NoSuchAlgorithmException | IOException ex) {
                System.out.println("ERROR en el almacenamiento de la clave DES: " + ex);
                JOptionPane.showMessageDialog(ventana, "ERROR en el almacenamiento de la clave DES: " + ex, "ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            ventana.setIconoCifrado(true);
            ventana.setProcesoTerminado();
            ventana.setTextoConsola("\nEL PROCESO HA FINALIZADO CORRECTAMENTE\nSe han generado 2 ficheros: 1 clave de encriptación y 1 fichero encriptado.");
        } else {
            JOptionPane.showMessageDialog(ventana, "Operación cancelada, debe indicar un directorio de destino");
        }
    }

    private static SecretKey generarClaveDES() throws NoSuchAlgorithmException {
        SecretKey clave = null;
        System.out.println("Obteniendo generador de claves con cifrado DES");
        ventana.setTextoConsola("Obteniendo generador de claves con cifrado DES");
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        System.out.println("Generando clave");
        ventana.setTextoConsola("Generando clave");
        clave = keygen.generateKey();
        return clave;
    }

    private static void encriptar(SecretKey clave, FileInputStream flujoLecturaOrigen, FileOutputStream flujoEscrituraCifrado) throws NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        System.out.println("Obteniendo objeto Cipher con cifrado DES");
        ventana.setTextoConsola("Obteniendo objeto Cipher con cifrado DES");
        Cipher desCipher = null;
        desCipher = Cipher.getInstance("DES");
        System.out.println("Configurando Cipher para encriptar");
        ventana.setTextoConsola("Configurando Cipher para encriptar");
        desCipher.init(Cipher.ENCRYPT_MODE, clave);
        System.out.println("Cifrando el fichero...");
        ventana.setTextoConsola("Cifrando el fichero...");
        byte[] buffer = new byte[8];
        int bytes_leidos = flujoLecturaOrigen.read(buffer);
        while (bytes_leidos != -1) {
            flujoEscrituraCifrado.write(desCipher.doFinal(buffer, 0, bytes_leidos));
            bytes_leidos = flujoLecturaOrigen.read(buffer);
        }
        flujoEscrituraCifrado.close();
    }

    private static void guardarClave(SecretKey clave, FileOutputStream flujoEscrituraClave) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeySpecException {
        System.out.println("Obteniendo factor�a de claves con cifrado DES");
        ventana.setTextoConsola("Obteniendo factoría de claves con cifrado DES");
        SecretKeyFactory keyfac = SecretKeyFactory.getInstance("DES");
        System.out.println("Generando keyspec");
        ventana.setTextoConsola("Generando keyspec");
        DESKeySpec keyspec = (DESKeySpec) keyfac.getKeySpec(clave, DESKeySpec.class);
        System.out.println("Salvando la clave en un fichero");
        ventana.setTextoConsola("Salvando la clave en un fichero");
        flujoEscrituraClave.write(keyspec.getKey());
        flujoEscrituraClave.close();
    }

    public static void abrirFicheroOrigen() {
        JFileChooser selector = new JFileChooser(rutaFicheroOrigen);
        selector.setDialogTitle("Abrir fichero de origen");
        selector.showOpenDialog(ventana);
        if (selector.getSelectedFile() != null) {
            ficheroOrigen = new File(selector.getSelectedFile().getPath());
            ventana.setTextoConsola("El fichero de origen que se utilizará se encuentra en:\n" + ficheroOrigen.getPath());
            ventana.setFicherosSeleccionados();
        }else{
            JOptionPane.showConfirmDialog(ventana, "Debe seleccionar un fichero a encriptar");
        }
    }

    public static void abrirFicheroClave(){
        try {
            java.awt.Desktop.getDesktop().edit(ficheroClave);
        } catch (IOException ex) {
            System.out.println("ERROR en la apertura del fichero clave: " + ex);
        }
    }
    
    public static void abrirFicheroCodificado(){
        try {
            java.awt.Desktop.getDesktop().edit(ficheroCifrado);
        } catch (IOException ex) {
            System.out.println("ERROR en la apertura del fichero codificado: " + ex);
        }
    }
    
    private static boolean asignarRutaFicheroClave() {
        JFileChooser selector = new JFileChooser(rutaFicheroClave);
        selector.setDialogTitle("Guardar fichero con la clave");    
        selector.showSaveDialog(ventana);
        if (selector.getSelectedFile() != null) {
            ficheroClave = new File(selector.getSelectedFile().getPath());
            ventana.setTextoConsola("El fichero con la clave se almacenará en:\n" + ficheroOrigen.getPath());
            return true;
        } else {
            return false;
        }
    }

    private static boolean asignarRutaFicheroCodificado() {
        JFileChooser selector = new JFileChooser(rutaFicheroCifrado);
        selector.setDialogTitle("Guardar fichero de origen codificado");    
        selector.showSaveDialog(ventana);
        if (selector.getSelectedFile() != null) {
            ficheroCifrado = new File(selector.getSelectedFile().getPath());
            ventana.setTextoConsola("El fichero con codificado se almacenará en:\n" + ficheroOrigen.getPath());
            return true;
        } else {
            return false;
        }
    }

}
