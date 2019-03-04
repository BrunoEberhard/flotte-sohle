package ch.openech.dancer.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Flyer {

	public Object id;

	public byte[] image;

	// TODO there has to be a better way to read everything from a stream
	public static Flyer read(String fileName) {
		Flyer flyer = new Flyer();
		java.util.List<Byte> bytes = new ArrayList<>();
		try (InputStream is = Flyer.class.getResource("/ch/openech/dancer/data/" + fileName).openStream()) {
			while (is.available() > 0) {
				bytes.add((byte) is.read());
			}
			flyer.image = new byte[bytes.size()];
			for (int i = 0; i < bytes.size(); i++) {
				flyer.image[i] = bytes.get(i);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return flyer;
	}
}
