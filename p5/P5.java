package p5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;



public class P5 {

	public static void main(String[] args) {
		String texto = leeTexto("C:\\Users\\Manuela\\git\\SeguridadInformaticaP5\\textos\\listaPruebas.txt");

		
		/* CODIFICACION DE ALFABETO FUENTE */
		String alf=".,;()¿?¡!-0123456789 aábcdeéfghiíjklmnñoópqrstuúvwxyzAÁBCDEÉFGHIÍJKLMNÑOÓPQRSTUÚVWXYZ";
		ArrayList<String> codificacion = construyeCodigo(alf, 5);
		
		/* CODIFICACION LISTA */
		String [] listaT = texto.split(",");	

		ArrayList<String> lista = new ArrayList<String>();
		
		for (int i = 0; i < listaT.length; i++) {
			lista.add(listaT[i]);
		}
	
		ArrayList<String> def = trocea(lista, 11);
		
		def.add(setCola(lista, 11)); 
		
		
		/*for (int j = 0; j < def.size(); j++) {
			System.out.println(def.get(j));
		}
		*/
		
		/* MATRIZ  (A) */
		String a = "1211101211101111101121101121";
		int [][] A = new int[4][7];
		int x = 0;
		
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
					A[i][j] = Character.getNumericValue(a.charAt(x));
					x++;
			}
		}
		
		/* MATRIZ DE CONTROL (H) */
		
		int [][] H = creaMatrizControlH(A);
		
		/*for (int s = 0; s < H.length; s++) {
			for (int ss = 0; ss < H[0].length; ss++) {
				System.out.print(H[s][ss] + "");
			}
			System.out.println(" ");
		}
		
		*/

		/*	------ TABLERO DE SINDROMES ------
		 * > Errores con peso <= 2, es decir que tengan como mucho 2 unos
		 * > y con una longitud de 11
		 * > sindrome = H * eT
		 */
		ArrayList<String> errores = errores(2, 11);
		
		/*for (int i = 0; i <  errores.size(); i++) {
			System.out.println(errores.get(i)+ " ");
		}*/
		
		ArrayList<Sindrome> tablero = construyeTablero(errores, H);
		
	/*
		System.out.println("tableroooooooooooooooooooooooooooooooooooooo\n");
		for (int i = 0; i < tablero.size(); i++) {
			System.out.println(tablero.get(i).getError() + " " + tablero.get(i).getSindrome());
		}
		System.out.println("tableroooooooooooooooooooooooooooooooooooooo\n");
		*/
		ArrayList<String> secCorr = getSecuenciasCorregidas(def, H, tablero);
		
		/* DECODIFICACION LINEAL */
		
		ArrayList<String> secDecoLin = decodificacionLineal(secCorr, 4);
		
		/* DECODIFICACION FUENTE */
		
		int longAlf = alf.length();
		double longMinBiBloque = Math.ceil(Math.log(longAlf) / Math.log(3));
		String aux = "";
		
		for (int y = 0; y < secDecoLin.size(); y++) {
			aux = aux + "" + secDecoLin.get(y); 	
		}
		
		ArrayList<String> secDecoTroc = divideDeXEnX(aux, longMinBiBloque);

		/* TRADUCCION */
		
		traduce(codificacion, alf, secDecoTroc);
		
	}
	
	public static String leeTexto(String dir) {
		String texto="";
		try {
			BufferedReader r = new BufferedReader(new FileReader(dir));
			String tmp ="";
			String bfread;
			
			while((bfread = r.readLine())!=null) {
			
				tmp = tmp+bfread;
			}
			
			texto = tmp;
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return texto;
	}
	
	public static ArrayList<String> decodificacionLineal( ArrayList<String> secCorr, int conserva){
		
		 ArrayList<String> secDecoLin= new  ArrayList<String>();
		 
		 for (int i = 0; i < secCorr.size(); i++) {
				secDecoLin.add(secCorr.get(i).substring(0, conserva));
		}
		 
		 return secDecoLin;
		
	}

	/*
	 * Troceamos de 11 en 11
	 */
	public static ArrayList<String> trocea(ArrayList<String> lista, int nTrozos) {
		
		ArrayList<String> listaTroceada = new ArrayList<String>();
		String aux = "";
		for (int i = 0; i < lista.size(); i++) {
			if(i%nTrozos >= 0) {
				aux = aux + "" + lista.get(i);
			}
			
			if(i%nTrozos == nTrozos-1) {
				listaTroceada.add(aux);
				aux = "";
			}
		}
		
		return listaTroceada;
	}
	
	public static String setCola(ArrayList<String> lista, int longPalabra) {
		String cola = "";
		int c = 0;
		
		for (int i = 0; i < lista.size(); i++) {
			
			if(i%longPalabra == longPalabra-1) {
				c++;
			}
			
			if(c >= lista.size()/longPalabra) {
				cola = cola + "" + lista.get(i);
			}
		}
		
		for (int j = cola.length(); j < longPalabra; j++) {
			cola = "0"+ cola;
		}
		
		return cola;
	}
	/*
	 * Busca si la secuencia introducida tiene error
	 * @return ""000000000000000"" si no hay error y si si hay devuleve el error patron correspoendiente
	 */
	public static String buscaSindrome(String secuencia, int[][]H, ArrayList<Sindrome> tablero) {
		
		String error = "00000000000"; //inicializamos a que el error es 0
		int [][] r = multiplicaMatrices(H, construyeTranspuesta(toVector(secuencia)),3);
		String r2 = vectorToString(construyeTranspuesta(r));
		
		//Buscamos en el tablero en sindromes
		for (int i = 0; i < tablero.size(); i++) {
			if(tablero.get(i).getSindrome().equals(r2)) { //hay error
				error = tablero.get(i).getError();
			}
		}
		return error;
	}
	
	/*
	 * @params secuencia a corregir y error patron
	 * Corregir la secuencia y hay que hacer la cuenta y−e moD 2. 
	 * @return La secuencia obtenida es ﬁable (SIN RUIDO).
	 */
	public static String correctorRuido(String secuencia, String errorPatron, int mod) {
		
		int[][] sec = toVector(secuencia);
		int[][] err = toVector(errorPatron);
		int[][] secSinRuido = new int[1][sec[0].length];
		
		
			for (int i = 0; i < secSinRuido[0].length; i++) {
				secSinRuido[0][i] = sec[0][i] - err[0][i];
				
				secSinRuido[0][i] = secSinRuido[0][i]%mod;
				if(secSinRuido[0][i] < 0) {
					secSinRuido[0][i] = secSinRuido[0][i]+mod;
				}
			}
		
		return (vectorToString(secSinRuido));
	}
	
	/*
	 * 
	 */

	public static ArrayList<String> getSecuenciasCorregidas(ArrayList<String> def,int[][]H, ArrayList<Sindrome> tablero){
		
		ArrayList<String> secuenciasCorregidas = new ArrayList<String>();
		for (int i = 0; i < def.size(); i++) {
			secuenciasCorregidas.add(i,correctorRuido(def.get(i), buscaSindrome(def.get(i), H, tablero),3));
		}
		return secuenciasCorregidas;
		
	}
	
	public static ArrayList<String> construyeCodigo(String a, int longPalabraAlfabetoFuente) {
		ArrayList<String> cod = new ArrayList<String>();
		String aux = "";
		
		for (int i = 0; i < a.length(); i++) {
			aux = Integer.toString(i,3);
			if(aux.length()<longPalabraAlfabetoFuente) {
				for (int j = aux.length(); j < longPalabraAlfabetoFuente; j++) {
					aux = "0"+aux;
				}
			}
			cod.add(aux);
			aux = "";
		}
		
		
		return cod;
	}

	public static void traduce(ArrayList<String> codificacion, String alf, ArrayList<String>  secCorr) {
		
		
		String result = "";
	
		for (int i = 0; i < secCorr.size(); i++) {
			
				for(int j = 0;j < codificacion.size(); j++) {
					if(codificacion.get(j).equals(secCorr.get(i))) {
						result = result + "" + alf.charAt(j);
					}	
				}
		}
		
		for (int k = 0; k < result.length(); k++) {
			
			if(result.charAt(k) == ' ' && result.charAt(k+1) == ' ') {
				System.out.println("");
				k++;
			}else{
				System.out.print(result.charAt(k));
			}	
		}	
	}

	private static ArrayList<String> divideDeXEnX(String def, double longMin) {
		ArrayList<String> div = new ArrayList<String>();
		String aux = "";
		
		for (int i = 0; i < def.length(); i++) {
			if(i%longMin >= 0) {
				aux = aux + "" + def.charAt(i);
			}
			
			if(i%longMin == longMin-1) {
				div.add(aux);
				aux = "";
			}	
		}
		return div;
	
	}
	
	private static int[][] creaMatrizGeneradora(int[][] a){
		
		//calculo matriz identidad
		int [][]iden = construyeMatrizIdentidad(4);
		
		//calculamos G =(iden | A)
				int m = iden.length; //6 filas
				int n = a[0].length; //9 columnas
				int c = 0;
				int[][] h = new int[m][iden[0].length+n];
				
				for (int i = 0; i < h.length; i++) { // recorre filas 9
					for (int j = 0; j < h[i].length; j++) { // recorre columnas 15
						if(j<iden[0].length) {
							h[i][j] = iden[i][j];
						}else {
							h[i][j] = a[i][c];
							c++;
						}
						
					}
					c = 0;
				}
				
				/*for (int t = 0; t < h.length; t++) {
					for (int k = 0; k < h[0].length; k++) {
						System.out.print(h[t][k] + " ");
					}
					System.out.println("");
				}*/
			return h; //6x15
				
	}
	
	private static int[][] creaMatrizControlH(int [][] a2) {
		
		//calculo matriz identidad
		int [][]iden = construyeMatrizIdentidad(7);
		
		//calculamos a2 transpuesta
		int[][] aT = construyeTranspuesta(a2);
		
		/*for (int k = 0; k < aT.length; k++) {
			for (int y = 0; y < aT[0].length; y++) {
				System.out.print(aT[k][y] + " ");
			}
			System.out.println("");
		}
		*/
		
		//Negamos a2
		
		int[][]atN = niegaMatriz(aT, 3);
		
		//calculamos H =(aTN | iden)
		int m = atN.length; //7 filas
		int n = iden[0].length; //4 columnas
		int c = 0;
		
		int[][] h = new int[m][atN[0].length+n];
		
		for (int i = 0; i < h.length; i++) { // recorre filas 7
			for (int j = 0; j < h[i].length; j++) { // recorre columnas 11
				if(j<atN[0].length) {
					h[i][j] = atN[i][j];
				}else {
					h[i][j] = iden[i][c];
					
					c++;
				}
				
			}
			c = 0;
		}
		return h; //7x11
	}
	
	private static int[][] construyeMatrizIdentidad(int s){
		int [][]iden = new int[s][s];
		
		for (int i = 0; i < iden.length; i++) {
			for (int j = 0; j < iden[i].length; j++) {
				if(i == j) {
					iden[i][j] = 1;
				
				}else {
					iden[i][j] = 0;
				}
			}
		}
		
		return iden;
	}
	
	private static int[][] construyeTranspuesta(int[][] matriz){
		int[][] matrizT = new int[matriz[0].length][matriz.length];
		
		for (int x=0; x < matriz.length; x++) {
			  for (int y=0; y < matriz[x].length; y++) {
				  matrizT[y][x] = matriz[x][y];
			  }
			}
		return matrizT;
	}
	
	public static ArrayList<String> errores(int peso, int longitud){
		ArrayList<String> listaErr= new ArrayList<String>();
		String aux = " ";
		int c = 0;
		int a = 0;

		
		while(!aux.equals("22222222222")) {
			aux = Integer.toString(c, 3);
			if(aux.length()<longitud) {
				for (int j = aux.length(); j < longitud; j++) {
					aux = "0"+aux;
				}
			}
			
			if(errorValido(aux, peso) == true) {
				listaErr.add(aux);
				a++;
				
			}
			c++;
		}
	
		return listaErr;
	}
	/*
	 * Cambiar en el if que sea la long del error 
	 */
	private static boolean errorValido(String e, int p) { 
		boolean valido = false;
		int peso = 0;
		
		if(e.length() == 11) {
			for (int i = 0; i < e.length(); i++) {
				if(e.charAt(i) == '1' || e.charAt(i) == '2') {
					peso++;
				}
			}
			
			if(peso <= p) {
				valido = true;
			}
		}	
		return valido;
	}
	
	public static ArrayList<Sindrome> construyeTablero(ArrayList<String> errores, int [][] H) {
		ArrayList<Sindrome> tablero = new ArrayList<Sindrome>();
		int [][] e;
		int [][] eT;
		int [][] m;
		
		for (int i = 0; i < errores.size(); i++) {
			e = toVector(errores.get(i));
			eT = construyeTranspuesta(e);
			
			m = multiplicaMatrices(H, eT, 3);
			tablero.add(new Sindrome(errores.get(i),vectorToString(construyeTranspuesta(m))));
		}
		return tablero;
	}
	
	private static int [][] toVector(String err) {
		int [][] v = new int [1][err.length()];
		for (int i = 0; i < v[0].length; i++) {
			v[0][i] = Character.getNumericValue(err.charAt(i));
		}
		return v;
	}
	
	private static int[][] multiplicaMatrices(int [][] h, int [][] m, int mod) {
		
		int[][] mResultado = new int[h.length][m[0].length];
	    if (h[0].length == m.length) { // si se pueden multiplicar
	        for (int i = 0; i < h.length; i++) {
	            for (int j = 0; j < m[0].length; j++) {
	                for (int k = 0; k < h[0].length; k++) {
	                    mResultado[i][j] += h[i][k] * m[k][j];
	                    mResultado[i][j] = mResultado[i][j]%mod; //congruente en modulo 2
	                }
	            }
	        }
	    }
	    return mResultado;
		
	}
	
	private static int[][] niegaMatriz(int[][]aT, int modulo){
		
		int[][] mNegada = new int[aT.length][aT[0].length];
		
		 for (int i = 0; i < aT.length; i++) {
	            for (int j = 0; j < aT[0].length; j++) {
	               mNegada[i][j] = -aT[i][j];
	               
	               if(mNegada[i][j] != 0) {
	            		mNegada[i][j] = mNegada[i][j] + modulo;
	               }
	            }
	        }
		
		return mNegada;
		
	}
	
	private static String vectorToString(int [][] v) {
		
		String s = "";
		
		for (int i = 0; i < v[0].length; i++) {
			
			s = s + "" + v[0][i];
		}
		return s;
	}
}
