
public class SerializationHelper {

	byte[] data;
	int index;
	
	public SerializationHelper(int size)
	{
		data = new byte[size];
		index = 0;
	}
	
	public boolean putInt(int... nums)
	{
		for(int num : nums)
		{
			int i;
			for(i=0; i<4 && index + i < data.length ; i++){
				 data[index + i] = (byte)((num >> 8*i) & 0xFF);
			 }
			index += i;
		}
		return index < data.length;
	}
	public boolean putByte(byte b)
	{
		if(index < data.length)data[index ++] = b;
		return index < data.length;
	}
	
	public byte[] getData()
	{
		return data;
	}
}
