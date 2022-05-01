package juegoDeLaVida;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;


/**
 * @author daniel
 * Clase que simula el juego de la vida.
 * 
 *
 */
public class JuegoDeLaVida {

	//Separador de caracteres a leer
	static final String SEPARADOR = ",";
	//Dimension del tablero de juego
	int dimension;
	//Ruta ingresada por el usuario con la matriz inicial.
	String ruta;
	//Matriz que contiene al nuevo estado despues de cada generacion.
	int[][] valores;
	//Numero de generaciones a jugar (introducido por el usuario)
	int generaciones;
	

	/**
	 * Constructor que ejecuta el juego entero, recibe el parametro:
	 * @param ruta, ruta al archivo csv con la matriz inicial.
	 */
	public JuegoDeLaVida(String ruta) {

		dimension = 0;
		Scanner sc = new Scanner(System.in);
		//si la dimension introducida por el usuario es menor a 50 se volvera a pedir que la introduzca
		while (dimension < 50) {
			System.out.println("Introduzca la dimension del tablero, debe ser un numero mayor o igual que 50");
			dimension = sc.nextInt();
		}

		System.out.println("Introduzca el numero de generaciones a jugar");
		generaciones = sc.nextInt();
		valores = new int[dimension][dimension];
		valores = generarTablero(ruta);
		while (valores == null) {
			System.out.println(" \n por favor introduzca una ruta valida");
			ruta = sc.next();
			valores = generarTablero(ruta);
		}
		int n_generaciones = 0;
		while (n_generaciones < generaciones) {
			System.out.println("pulse 1 para continuar");
			int tecla = sc.nextInt();
			if (tecla == 1) {
				imprimirTablero();
				n_generaciones++;
			}
			if (n_generaciones != generaciones) {
				generacion();
			}
			if (n_generaciones == generaciones) {
				System.out.println("Fin del juego gracias por participar \n");
				System.out.println("Por favor introduzca la ruta a la que desea exportar el resultado del juego");
				String rutaSalida = sc.next();
				System.out.println(escribirAFicheroV2(valores, rutaSalida));
				sc.close();
			}
		}

	}

	/**
	 * Metodo que genera el tablero inicial a partir del csv con la matriz inicial recibe el parametro:
	 * @param ruta, ruta al archivo csv con la matriz inicial.
	 * @return devuelve una matriz de dos dimensiones con la matriz inicial
	 */
	public int[][] generarTablero(String ruta) {

		try {
			FileReader fr = new FileReader(ruta);
			BufferedReader br = new BufferedReader(fr);

			String linea;
			String[] fila;
			int numFila = 0;

			while ((linea = br.readLine()) != null) {

				fila = linea.split(",");
				for (int numCol = 0; numCol < dimension; numCol++) {
					if (numCol < fila.length) {
						valores[numFila][numCol] = Integer.parseInt(fila[numCol].trim());
					} else {
						// luego de leer todas las columnas el resto se rellenan con ceros
						valores[numFila][numCol] = 0;
					}

				} //incremento al numero de fila
				numFila++;

			} // fin del while
				// luego de leer todas las columnas el resto se rellenan con ceros
			if (numFila < dimension) {
				for (int i = numFila; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						valores[i][j] = 0;
					}

				}
			} 
			fr.close();
		} catch (Exception e) {
			System.out.println("Excepcion leyendo fichero " + ruta + ": " + e);

		}
    return valores;
	}



	/**
	 * metodo que imprime el el tablero con el estado actual de la matriz
	 * 
	 */
	public void imprimirTablero() {
		System.out.println("---");
		for (int i = 0; i <dimension; i++) {
			String linea = "|";
			for (int j = 0; j < dimension; j++) {
				if (valores[i][j] == 0) {
					linea += ".";
				} else {
					linea += "*";
				}
			}
			linea += "|";
			System.out.println(linea);
		}
		System.out.println("---\n");
	}

	/**
	 * Metodo que sirve para contar la cantidad de vecinos vivos que tiene una celda, recibe los parametros:
	 * @param x numero de fila
	 * @param y numero de columna
	 * @return devuelve el total de vecinos vivos.
	 */
	public int contarVecinosVivos(int x, int y) {
		int conteo = 0;

		conteo += getEstado(x - 1, y - 1);
		conteo += getEstado(x, y - 1);
		conteo += getEstado(x + 1, y - 1);

		conteo += getEstado(x - 1, y);
		conteo += getEstado(x + 1, y);

		conteo += getEstado(x - 1, y + 1);
		conteo += getEstado(x, y + 1);
		conteo += getEstado(x + 1, y + 1);

		return conteo;
	}

	/**
	 * Metodo que sirve para saber el estado vivo o muerto de una celda, recibe los parametros:
	 * @param x numero de fila
	 * @param y numero de columna
	 * @return
	 */
	public int getEstado(int x, int y) {
		if (x < 0 || x >= dimension) {
			return 0;
		}

		if (y < 0 || y >= dimension) {
			return 0;
		}

		return valores[x][y];
	}

	/**
	 * Metodo que genera una nueva matriz teniendo en cuenta las reglas del juego.
	 * 
	 */
	public void generacion() {
		int[][] otroTablero = new int[dimension][dimension];

		for (int y = 0; y < dimension; y++) {
			for (int x = 0; x < dimension; x++) {
				int vecinosVivos = contarVecinosVivos(x, y);

				if (getEstado(x, y) == 1) {
					if (vecinosVivos < 2) {
						otroTablero[x][y] = 0;
					} else if (vecinosVivos == 2 || vecinosVivos == 3) {
						otroTablero[x][y] = 1;
					} else if (vecinosVivos > 3) {
						otroTablero[x][y] = 0;
					}
				} else {
					if (vecinosVivos == 3) {
						otroTablero[x][y] = 1;
					}
				}

			}
		}

		valores = otroTablero;
	}

	/**
	 * Metodo que escribe un archivo csv con el resultado final del juego recibe los parametros:
	 * @param valores, matriz de dos dimensiones con el estado final despues de aplicar las reglas del juego 
	 * @param ruta, ruta escrita por el usuario donde se guardara el archivo csv
	 * @return
	 */
	static boolean escribirAFicheroV2(int[][] valores, String ruta) {

		try (Formatter escritor = new Formatter(new File(ruta))) {
			// abrimos el fichero en modo "destructivo"
			// recorrido del array por filas
			for (int i = 0; i < valores.length; i++) {

				for (int k = 0; k < valores[i].length - 1; k++) {

					escritor.format("%d%s", valores[i][k], SEPARADOR);
				}
				// para el último elemento, no agregamos el separador al final
				escritor.format("%d", valores[i][valores[i].length - 1]);
				// agregamos un salto de línea, salvo si es la última del fichero
				if (i != valores.length - 1) {
					escritor.format("%n");
				}

			} // fin de recorrido de líneas
			return true;

		} catch (IOException e) {
			System.out.println("Se produjo el siguiente error al acceder al fichero" + e.getMessage());
			return false;

		}
		// no necesitamos finally al haber usado la estructura try-resources
	}

	public static void main(String[] args) {

		JuegoDeLaVida simulation = new JuegoDeLaVida("C:\\Users\\danie\\Desktop\\juego.csv");
	}

}
