package org.opengpx.lib;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class CacheIndexItem 
{
	public String code;
	public String type;
	public String container;
	public String name;
	public Double difficulty;
	public Double terrain;
	public Integer favoritePoints = -1;
	public float vote = 0.0f;
	public Double latitude;
	public Double longitude;
	public Boolean isAvailable = true;
	public Boolean isArchived = false;
	public Boolean isMemberOnly = false;
}
