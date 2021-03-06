package com.mlab.gpx.impl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.gpx.impl.srs.EllipsoidWGS84;

public class Util {
	private final static Logger LOG = Logger.getLogger(Util.class);
	
	public static String secondsToHMSString(long seconds) {
		StringBuilder builder = new StringBuilder();
		double[] hms = secondsToHMS(seconds);
		if(hms[0]>0) {
			builder.append((int)hms[0]);
			builder.append(" h");
		}
		if(hms[1]>0) {
			builder.append(" ");
			builder.append((int)hms[1]);
			builder.append(" m");
		}
		if(hms[2]>0) {
			builder.append(" ");
			builder.append((int)hms[2]);
			builder.append(" s");
		}
		if(builder.toString().isEmpty()) {
			builder.append("0 s");
		}
		return builder.toString();
	}
	public static double[] secondsToHMS(long seconds) {
		double hours = secondsToHours(seconds);
		double completeHours = Math.floor(hours);
		long secondsReminder = seconds - (long)completeHours*60*60;
		double minutesReminder = secondsToMinutes(secondsReminder);
		double completeMinutes = Math.floor(minutesReminder);
		secondsReminder = secondsReminder - (long)completeMinutes*60;
		return new double[]{completeHours,completeMinutes,secondsReminder};
	}
	public static double secondsToHours(long seconds) {
		return (((double)seconds)/60.0/60.0);
		
	}
	public static double secondsToMinutes(long seconds) {
		return (((double)seconds)/60.0);
		
	}
	/**
	 * Formatea un double a los digitos y precisión deseados, 
	 * sustituyendo la coma decimal por el punto decimal.<br/>
	 * Los números se redondeán al número de decimales pedido.
	 * @param value double valor
	 * @param digits número total de dígitos
	 * @param decimals número de decimales
	 * @return cadena con el número formateado xxx.xxx
	 * Si value es NaN o infinito, o digits<=0 o decimals<0 
	 * arroja IllegalArgumentException
	 */
	public static String doubleToString(double value, int digits, int decimals) {
		// System.out.println(value);
		if(Double.isNaN(value) || Double.isInfinite(value) || digits <= 0 || decimals <0) {
			throw new IllegalArgumentException();
		}
		StringBuilder builder = new StringBuilder();
		builder.append("%");
		builder.append(String.format("%d", digits));;
		builder.append(".");
		builder.append(String.format("%d", decimals));;
		builder.append("f");
		
		String cad = String.format(builder.toString(), value).replace(',', '.').trim();
		return cad;
	}
	/**
	 * Extrae la fecha del vídeo a partir del nombre del fichero
	 * en formato yyyyMMdd_HHmmss.mp4
	 * @param file fichero de video a comprobar
	 * @return long con la fecha o -1l si hay error 
	 */
	public static long startTimeFromFilename(File file) {
		String filename = fileNameWithoutExtension(file);
		Date date=null;
		long t = -1l;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			date=df.parse(filename);
			t = date.getTime();
		} catch (ParseException e1) {
			t = -1l;
		}
		return t;
	}
	
    /**
     * Devuelve una cadena de la forma "yyyyMMdd_HHmmss" con
     * la fecha y hora GMT correspondiente a la fecha pasada
     * como argumento.
     * @param date Date 
     * @return Cadena "yyyyMMdd_HHmmss" con la fecha GMT
     */
    public static String getTimeStamp(Date date, boolean gmt) {
    	DateFormat timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
    	if(gmt) {
    		timeStamp.setTimeZone(TimeZone.getTimeZone("GMT"));
    	}
    	return timeStamp.format(date);
    }

    /**
     * Devuelve una cadena en la forma 2012-10-09T12:00:23
     * @param t
     * @param gmt
     * @return Devuelve una cadena en la forma 2012-10-09T12:00:23
     */
 	public static String dateTimeToString(long t, boolean gmt) {    	
    	String cad=Util.dateToString(t,gmt);
    	cad+="T"+Util.timeToString(t,gmt);
    	return cad;
    }
    /**
     * Formatea una fecha 'yyyy-MM-ddThh:mm:ss.ssZ'
     * @param t tiempo en milisegundos de la fecha
     * @return 'yyyy-MM-ddThh:mm:ss.ssZ'
     */
    public static String dateTimeToStringGpxFormat(long t) {
    	SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'");
   		format.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return format.format(new Date(t));
    }
    /**
     * Extrae la fecha y hora de una cadena en formato de fecha GPX
     * @param cadGpxDateTime Cadena de fecha y hora en la forma:<br>
     * yyyy-MM-dd'T'HH:mm:ss.ss'Z'
     * @return long con la fecha-hora o -1l si hay errores
     */
    public static long parseGpxDate(String cadGpxDateTime) {
    	SimpleDateFormat format = null;
    	if(cadGpxDateTime.length()==23) {
    		format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'");	
    	} else if (cadGpxDateTime.length()==22) {
        	format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'");    		
    	} else if (cadGpxDateTime.length()==20) {
        	format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");    		
    	}
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date;
		long t = -1l;
		try {
			date = format.parse(cadGpxDateTime);
			t = date.getTime();
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return t;
    }
    /**
     * 
     * @param t Milisegundos de la hora
     * @param gmt si true, se utilizará hora GMT
     * @return Cadena con la hora en formato hh:mm:ss
     */
    public static String timeToString(long t, boolean gmt) {
    	//Log.i("HAL","Util.timeToString()");
    	Calendar cal=Calendar.getInstance();
    	cal.setTimeInMillis(t);
    	if(gmt) {
    		cal.setTimeZone(TimeZone.getTimeZone("gmt"));
    	}
    	String date=String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+":"+
    			String.format("%02d", cal.get(Calendar.MINUTE))+":"+
    			String.format("%02d", cal.get(Calendar.SECOND));
    	//Log.d("HAL","Time:"+date);
        return date;
    }
    public static String dateToString(long t, boolean gmt) {
    	//Log.i("HAL","Util.dateToString()");
    	Calendar cal=Calendar.getInstance();
    	if(gmt) {
    		cal.setTimeZone(TimeZone.getTimeZone("gmt"));
    	}
    	cal.setTimeInMillis(t);
    	String date=cal.get(Calendar.YEAR)+"-"+String.format("%02d", cal.get(Calendar.MONTH)+1)+"-"+
    			String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
    	//Log.d("HAL","Date:"+date);
        return date;
    }

    /**
     * Extrae la extensión de un fichero
     */
    public static String getFileExtension(File file) {
    	String ext = "";
    	int index = file.getName().lastIndexOf('.');
    	if(index != -1) {
        	ext = file.getName().substring(index+1, file.getName().length());    		
    	}
    	//System.out.println(ext);
    	return ext;
    }
    
    /**
	 * Devuelve el nombre del fichero sin extensión
	 * @param file Fichero
	 * @return String Nombre sin extensión del fichero
	 */
	public static String fileNameWithoutExtension(File file) {
		String filename = file.getName();
		String ext = Util.getFileExtension(file);
		try {
			int end = filename.length()-ext.length()-1;
			filename = filename.substring(0,end);
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
			filename = "";
		}
		return filename;		
	}
    
    /**
	 * Escribe una cadena de texto en un fichero
	 * 
	 * @param filename
	 *            String Nombre del fichero
	 * @param cad
	 *            String Cadena de texto a escribir
	 * @return 1 si todo va bien, negativo o cero en caso contrario
	 */
	public static int write(String filename, String cad) {
		LOG.debug("Util.write(): " + filename);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write(cad + "\n");
			writer.close();
		} catch (FileNotFoundException fe) {
			System.out.println("File " + filename + " not found.\n"
					+ fe.getMessage());
			return -1;
		} catch (NumberFormatException ne) {
			System.out.println("Number format error. " + ne.getMessage());
			return -2;
		} catch (Exception e) {
			System.out.println("Unidentified error. " + e.getMessage());
			return -3;
		}
    
		return 1;
	}

	/**
	 * Lee un fichero de texto y lo entrega en forma de un String
	 * @param file File fichero de texto
	 * @return String con el fichero leido o una cadena vacía
	 */
	public static String readFileToString(File file) {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader;
		String line="";	
		try {
			reader = new BufferedReader(new FileReader(file));
			line="";
			while((line=reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			reader.close();
		} catch (FileNotFoundException fe) {
			//LOG.info("File "+filename+" not found.\n"+fe.getMessage());
			return "";
		} catch (NumberFormatException ne) {
			//LOG.info("Number format error. "+ne.getMessage());
			return "";
		} catch (Exception e) {
			//LOG.info("Unidentified error. "+e.getMessage());
			return "";
		}
		return builder.toString();
		
	}
	public static String readFileToString(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }	
	/**
	 * Lee una matriz de doubles desde un fichero CSV
	 * @param filename Nombre del fichero
	 * @param delimiter Delimitador de campos
	 * @return Matriz con los doubles leidos
	 */
	public static double[][] readCsvDoubles(String filename, String delimiter) {
		//LOG.info("Galib.read("+filename+", "+delimiter+")");
		ArrayList<double[]> arrvpoint=new ArrayList<double[]>();
		BufferedReader reader;
		String line="";	
		int numcolumns=0;
		double d[];
		try {
			reader = new BufferedReader(new FileReader(filename));
			line="";
			while((line=reader.readLine()) != null) {
				String[] arr=line.split(delimiter);
				d = new double[arr.length];
				for(int i=0; i<d.length; i++) {
					d[i]=Double.parseDouble(arr[i].trim());
				}
				arrvpoint.add(d);
				numcolumns=d.length; //
			}
			reader.close();
		} catch (FileNotFoundException fe) {
			//LOG.info("File "+filename+" not found.\n"+fe.getMessage());
			return null;
		} catch (NumberFormatException ne) {
			//LOG.info("Number format error. "+ne.getMessage());
			return null;
		} catch (Exception e) {
			//LOG.info("Unidentified error. "+e.getMessage());
			return null;
		}
		double[][] result= new double[arrvpoint.size()][numcolumns];
		return arrvpoint.toArray(result);
	}
	
	 /**
	 * Lee un recurso almacenado en /src/main/resources y lo devuelve como fichero
	 * que queda grabado en el directorio raiz de la aplicación
	 * @param filename nombre del fichero (sin path, solo nombre)
	 * @return File del fichero grabado en el directorio de la aplicación
	 */
	public static File readResourceFile(String filename) {
		//LOG.info("readResourceFile():"+filename);
		File file=new File(filename);
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(filename);
			OutputStream out = new FileOutputStream(file);
			 
			int read = 0;
			byte[] bytes = new byte[1024];
		 
			while ((read = is.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		 
			is.close();
			out.flush();
			out.close();
		 
			//LOG.info("   New file created");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			//LOG.severe("Error, can't read file");					    
	    }
		return file;
	}

	public static List<Double> arrayDoublesToList(double[] values) {
		if(values==null) {
			return null;
		}
		List<Double> list = new ArrayList<Double>();
		for(Double value:values) {
			list.add(value);
		}
		return list;
	}

	/**
	 * Calcula el Rumbo para ir del primer punto al segundo mediante
	 * navegación loxodrómica.
	 * 
	 * @param lon1 Longitud del primer punto en grados
	 * @param lat1 Latitud del primer punto en grados
	 * @param lon2 Longitud del segundo punto en grados
	 * @param lat2 Latitud del segundo punto en grados
	 * 
	 * @return Rumbo en grados medido del Norte hacia el Este
	 */
	public static double bearing(double lon1, double lat1, double lon2, double lat2) {
		double incL = lon2 - lon1;
		// Resolver rumbos 0 y 180
		if(incL == 0.0) {
			if(lat1<lat2) {
				return 0.0;
			} else if (lat1>lat2) {
				return 180.0;
			} else {
				// incL = 0.0; inclat=0.0; => R=0.0;
				return 0.0;
			}
		}
		// Resolver rumbos 90 y 270
		double incLat = lat2 - lat1;
		if(incLat == 0.0) {
			if(lon1 < lon2) {
				return 90.0;
			} else if ( lon1 > lon2) {
				return 270.0;
			} else {
				return 0.0;
			}
		}
		double incLonradians = (lon2 - lon1) * Math.PI / 180.0;
		double incLatradians = (lat2 - lat1) * Math.PI / 180.0;
		double lm = (lat1+lat2)/2.0;
		double lmradians = lm * Math.PI / 180.0;
		double coslm = Math.cos(lmradians);
		double Apradians = incLonradians * Math.abs(coslm);
		double tanR = Apradians / incLatradians;
		double Rradians = Math.atan(tanR);
		double R = Rradians * 180.0 / Math.PI;
		if (R>0) {
			if(Apradians<0) {
				// tercer cuadrante
				R = 180.0 + R;
			}
		} else {
			if(Apradians>0) {
				// Segundo cuadrante
				R = 180.0 + R;
			} else {
				// Cuarto cuadrante
				R = 360.0 + R;
			}
		}
		return R;		
	}
	public static double bearing(WayPoint wp1, WayPoint wp2) {
		return Util.bearing(wp1.getLongitude(), wp1.getLatitude(), wp2.getLongitude(), wp2.getLatitude());
	}
	/**
	 * Calcula la velocidad entre dos WayPoint mediante la fórmula
	 * distancia/tiempo. La distancia la calcula con la fórmula de distancia 3D.
	 * 
	 * @param wp1 Primer WayPoint
	 * @param wp2 Segundo WayPoint
	 * 
	 * @return Velocidad en metros/segundos
	 */
	public static double speed(WayPoint wp1, WayPoint wp2) {
		double dist = Util.dist3D(wp1, wp2);
		double tseconds = (double)((wp2.getTime() - wp1.getTime()) / 1000l);
		return dist/tseconds;
	}
	/**
	 * Devuelve un double con los valores [minAltitude, maxAltitude, averageAltitude] 
	 * de los WayPoint de un TrackSegment
	 * 
	 * @param tsegment TrackSegment que se quiere analizar
	 *
	 * @return [minAltitude, maxAltitude, averageAltitude]
	 */
	public static double[] minmaxAltitude(TrackSegment tsegment) {
		if(tsegment==null || tsegment.size()<2) {
			return null;
		}
		double maxAltitude, minAltitude, altitudeAcc, distanceAcc;
		WayPoint wp = tsegment.getStartWayPoint();
		maxAltitude = wp.getAltitude();
		minAltitude = wp.getAltitude();
		altitudeAcc = 0.0;
		distanceAcc = 0.0;
		for(int i=1; i<tsegment.size(); i++) {
			WayPoint newwp = tsegment.getWayPoint(i);
			double dist = Util.dist3D(wp, newwp);
			distanceAcc += dist;
			double hmed = (wp.getAltitude()+newwp.getAltitude())/2.0;
			altitudeAcc += hmed * dist;
			if(newwp.getAltitude()>maxAltitude) {
				maxAltitude = newwp.getAltitude();
			}
			if(newwp.getAltitude()< minAltitude) {
				minAltitude = newwp.getAltitude();
			}
			wp = newwp;
		}
		double averageAltitude= altitudeAcc / distanceAcc;
		return new double[]{minAltitude, maxAltitude, averageAltitude};
	}
	/**
	 * Devuelve un double con los valores [minSpeed, maxSpeed, averageSpeed] 
	 * de los WayPoint de un TrackSegment
	 * 
	 * @param tsegment TrackSegment que se quiere analizar
	 *
	 * @return [minSpeed, maxSpeed, averageSpeed]
	 */
	public static double[] minmaxSpeed(TrackSegment tsegment) {
		if(tsegment==null || tsegment.size()<2) {
			return null;
		}
		double maxv, minv, lastv, avgv, distacc;
		WayPoint wp = tsegment.getStartWayPoint();
		maxv = 0.0;
		minv = 0.0;
		avgv = 0.0;
		lastv = 0.0;
		distacc = 0.0;
		for(int i=1; i<tsegment.size(); i++) {
			WayPoint newwp = tsegment.getWayPoint(i);
			double newv = Util.speed(wp, newwp);
			double dist = Util.dist3D(wp, newwp);
			distacc += dist;
			if(i==0) {
				avgv = newv * dist; 
			} else {
				avgv += (lastv + newv)/ 2.0 * dist;
			}
			if(newv > maxv) {
				maxv = newv;
			}
			if(newv < minv) {
				minv = newv;
			}
			lastv = newv;
			wp = newwp;
		}
		avgv = avgv / distacc;
		return new double[]{minv, maxv, avgv};
	}

	/**
	 * Calcula un vector con las pendientes de los tramos del TrackSegment. Las 
	 * pendientes se calculan como el cociente del incremento de altitud partido
	 * por la distancia de las proyeccciones horizontales de los puntos. Se utiliza
	 * la proyección UTM. Si el parámetro asPercentage es true los resultados de
	 * las pendientes se dan en tanto por ciento.
	 * 
	 * @param segment TrackSegment a procesar
	 * 
	 * @return List<double> con las pendientes de cada segmento. Los resultados serán
	 * en tanto por uno o tanto por ciento según el valor del parámetro asPercentage.
	 *  El número de segmentos es una unidad menos que el número de puntos del 
	 *  TrackSegment. Si los datos de entrada no son correctos se devuelve null.
	 */
	public static List<Double> slopesVector(TrackSegment segment, boolean asPercentage) {
		if(segment==null || segment.size()<2) {
			return null;
		}
		List<Double> slopes = new ArrayList<Double>();
		for(int i=0; i<segment.size()-1; i++) {
			double dist = Util.distUtmWGS84(segment.getWayPoint(i), segment.getWayPoint(i+1));
			double inch = segment.getWayPoint(i+1).getAltitude() - segment.getWayPoint(i).getAltitude();	
			double slope = 2e10;
			if(dist != 0.0) {
				slope = inch / dist;
				if(asPercentage) {
					slope = slope * 100.0;
				}
				slopes.add(slope);				
			} 
		}
		return slopes;
	}
	/**
	 * Calcula un vector con los rumbos en grados de cada punto del
	 * track con el siguiente. La dimensión de la solución
	 * es uno menos que el número de puntos del track.
	 * 
	 * @param segment
	 * 
	 * @return
	 */
	public static List<Double> bearingsVector(TrackSegment segment) {
		if(segment==null || segment.size()<2) {
			return null;
		}
		List<Double> bearings = new ArrayList<Double>();
		for(int i=0; i<segment.size()-1; i++) {
			WayPoint wp1 = segment.getWayPoint(i);
			WayPoint wp2 = segment.getWayPoint(i+1);			
			double bearing = Util.bearing(wp1, wp2);
			bearings.add(bearing);				
		}
		return bearings;
	}
	public static List<Double> speedsVector(TrackSegment segment) {
		if(segment==null || segment.size()<2) {
			return null;
		}
		List<Double> speeds = new ArrayList<Double>();
		for(int i=0; i<segment.size()-1; i++) {
			WayPoint wp1 = segment.getWayPoint(i);
			WayPoint wp2 = segment.getWayPoint(i+1);			
			double speed = Util.speed(wp1, wp2);
			speeds.add(speed);				
		}
		return speeds;
	}
	
	/**
	 * Devuelve el ángulo inicial a seguir en una navegaciçon ortodrómica
	 * @param lon1, lat1, lon2, lat2 Longitudes y latitudes de los puntos
	 * inicial y final en grados
	 * 		
	 * @return El ángulo inicial a seguir en grados sexagesimales
	 */
	public static double orthodromicStartAngle(double lon1, double lat1, double lon2, double lat2) {
		double llon1 = lon1 * Math.PI / 180.0;
		double llat1 = lat1 * Math.PI / 180.0;
		double llon2 = lon2 * Math.PI / 180.0;
		double llat2 = lat2 * Math.PI / 180.0;
		double incL = (llon2 - llon1);
		double ctgStartangle = (Math.tan(llat2)*Math.cos(llat1)-Math.sin(llat1)*Math.cos(incL)) / 
				(Math.sin(incL));
		double startAngle = Math.atan(1/ctgStartangle);
		return startAngle * 180.0 / Math.PI ;
	}
	/**
	 * Orthodromic distance in meters from one point to other
	 * 
	 * @param lon1, lat1, lon2, lat2 Longitudes y latitudes de los puntos
	 * inicial y final en grados
	 * 		
	 * @return La distancia en metros entre el punto inicial y el punto final
	 * a lo largo de un círculo máximo
	 */
	public static double orthodromicDistance(double lon1, double lat1, double lon2, double lat2) {
		double llon1 = lon1 * Math.PI / 180.0;
		double llat1 = lat1 * Math.PI / 180.0;
		double llon2 = lon2 * Math.PI / 180.0;
		double llat2 = lat2 * Math.PI / 180.0;
		double incL = (llon2 - llon1);
		double cosD = Math.sin(llat1)*Math.sin(llat2)+ Math.cos(llat1)*Math.cos(llat2)*Math.cos(incL);
		double Dminutes = Math.acos(cosD)*180.0/Math.PI*60.0;
		double dist = Dminutes * 1851.0; 
		return dist;
	}
	/**
	 * Calcula la distancia loxodrómica entre la proyección horizontal de dos puntos,
	 *  conocidas su coordenadas geográficas. Se utiliza el método náutico de estima 
	 *  'Alrededor del apartamiento, apareció la madre de luis':<br/>
	 *  
	 *  Apartamiento = incLongitud  x cos(latitud_media) <br/>
	 *  Rumbo = atan( Apartamiento / incLatitud ) <br/>
	 *  Distancia = sqrt( Apart**2 x incLat**2 ) <br/>
	 *  
	 * @param lon1 longitud del primer punto en grados 
	 * @param lat1 latitud del primer punto en grados
	 * @param lon2 longitud del segundo punto en grados
	 * @param lat2 latitud del segundo punto en grados
	 * 
	 * @return Devuelve la distancia en metros
	 */
	public static double loxodromicDistance(double lon1, double lat1, double lon2, double lat2) {
		double incL = (lon2 - lon1)*Math.PI / 180.0; // Incremento de Longitud en radianes
		double lm = (lat1 + lat2) / 2.0 * Math.PI / 180.0 ; // Latitud media en radianes
		double A = incL * Math.cos(lm); // Apartamiento 
		double inclat = (lat2 - lat1) * Math.PI / 180.0; // Incremento de latitud en radianes
		double D = Math.sqrt(A*A + inclat*inclat); // Distancia en radianes
		double distmillas = D * 180.0 / Math.PI * 60.0; // Distancia en minutos = millas
		double distmeters = distmillas * 1852.0;
		return distmeters;
	}
	public static double loxodromicDistance(WayPoint wp1, WayPoint wp2) {
		if(wp1 == null || wp2 == null) {
			return Double.NaN;
		}
		return Util.loxodromicDistance(wp1.getLongitude(), wp1.getLatitude(),	
				wp2.getLongitude(), wp2.getLatitude());
	}
	
	/**
	 * Calcula la distancia loxodromica y luego hace pitagoras con la altitud
	 * 
	 * @param lon1 longitud del primer punto en grados 
	 * @param lat1 latitud del primer punto en grados
	 * @param alt1 altitud en metros del primer punto
	 * @param lon2 longitud del segundo punto en grados
	 * @param lat2 latitud del segundo punto en grados
	 * @param alt2 altitud en metros del segundo punto

	 * @return Distancia en metros
	 */
	public static double dist3D(double lon1, double lat1, double alt1, double lon2, double lat2, double alt2) {
		double dh = Util.loxodromicDistance(lon1, lat1, lon2, lat2);
		double inch = alt2-alt1;
		double d3d = Math.sqrt(dh*dh + inch*inch);
		return d3d;
	}
	public static double dist3D(WayPoint wp1, WayPoint wp2) {
		if(wp1 == null || wp2 == null) {
			return Double.NaN;
		}
		return Util.dist3D(wp1.getLongitude(), wp1.getLatitude(), wp1.getAltitude(),	
				wp2.getLongitude(), wp2.getLatitude(), wp2.getAltitude());
	}

	public static double distCartesian(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	
	public static double[] proyUtmWGS84(double lon, double lat) {
		EllipsoidWGS84 ell = new EllipsoidWGS84();
		return ell.proyUTM(lon, lat);
	}
	/**
	 * Calcula la proyección UTM de los puntos de un TrackSegment 
	 *
	 * @param segment TrackSegment que se quiere proyectar
	 * 
	 * @return List<double[]> en el que cada elemento es un double[2] 
	 * con las coordenadas x,y proyectadas del way point correspondiente
	 * en el TrackSegment. Si el waypoint o su proyección son nulos se añade
	 * un double[]{0.0,0.0}.<br>
	 */
	public static List<double[]> proyUTMWGS84(TrackSegment segment) {
		if(segment==null || segment.size()==0) {
			return null;
		}
		List<double[]> proy = new ArrayList<double[]>();
		for(int i=0; i< segment.size(); i++) {
			WayPoint wp = segment.getWayPoint(i);
				if(wp != null) {
				double[] p = Util.proyUtmWGS84(wp.getLongitude(), wp.getLatitude());
				if(p != null) {
					proy.add(p);
				} else {
					proy.add(new double[]{0.0,0.0});
				}
			} else {
				proy.add(new double[]{0.0,0.0});
			}
		}
		return proy;
	}
	public static double distUtmWGS84(double lon1, double lat1, double lon2, double lat2) {
		double[] xy1 = Util.proyUtmWGS84(lon1, lat1);
		double[] xy2 = Util.proyUtmWGS84(lon2, lat2);
		return Util.distCartesian(xy1[0], xy1[1], xy2[0], xy2[1]);
	}
	public static double distUtmWGS84(WayPoint wp1, WayPoint wp2) {
		double lon1 = wp1.getLongitude();
		double lat1 = wp1.getLatitude();
		double lon2 = wp2.getLongitude();
		double lat2 = wp2.getLatitude();
		
		return Util.distUtmWGS84(lon1, lat1, lon2, lat2);	
	}
}
