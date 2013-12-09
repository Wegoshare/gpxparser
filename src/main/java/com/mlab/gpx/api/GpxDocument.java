package com.mlab.gpx.api;

import java.util.List;

import org.w3c.dom.Document;

import com.mlab.tesis.java.gpx.data.Extensions;
import com.mlab.tesis.java.gpx.data.Metadata;
import com.mlab.tesis.java.gpx.data.Route;
import com.mlab.tesis.java.gpx.data.Track;

/**
 * Interface for GpxDocuments. A GpxDocument has one Metadata element,
 * a List of WayPoint's, a List of Route's, a List of Tracks and one 
 * Extensions element.<br>
 * It has a method to get a java DomDocument and methods to add a WayPoint, a Route or a Track
 * @author shiguera
 *
 */
public interface GpxDocument extends GpxNode {
	
	Document getDomDocument();
	Metadata getMetadata();
	List<WayPoint> getWayPoints();
	List<Route> getRoutes();
	List<Track> getTracks();
	Extensions getExtensions();
	
	void addTrack(Track track);
	void addWayPoint(WayPoint wp);
	void addRoute(Route rte);

}