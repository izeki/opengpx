package org.opengpx.lib.geocache;

public class Attribute 
{
	public int id;
	public boolean flag;
	public String name;

	@Override
	public String toString()
	{
		// return String.format("%d %s (%b)", this.id, this.name, this.flag);
		return String.format("%s (%b)", this.name, this.flag);
	}
}

