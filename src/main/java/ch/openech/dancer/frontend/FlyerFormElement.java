package ch.openech.dancer.frontend;

import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.Frontend.IComponent;
import org.minimalj.frontend.Frontend.Input;
import org.minimalj.frontend.Frontend.InputComponentListener;
import org.minimalj.frontend.form.element.AbstractFormElement;
import org.minimalj.frontend.form.element.FormElementConstraint;

import ch.openech.dancer.model.Flyer;

public class FlyerFormElement extends AbstractFormElement<Flyer> {

	private final Input<byte[]> input;
	private final FormElementConstraint constraint;
	private Flyer flyer;

	public FlyerFormElement(Object key, boolean editable) {
		super(key);
		this.constraint = new FormElementConstraint(3, 3);
		input = Frontend.getInstance().createImage(editable ? new ImageFieldChangeListener() : null);
	}

	@Override
	public FormElementConstraint getConstraint() {
		return constraint;
	}

	@Override
	public void setValue(Flyer flyer) {
		this.flyer = flyer;
		input.setValue(flyer != null ? flyer.image : null);
	}

	@Override
	public Flyer getValue() {
		byte[] img = input.getValue();
		if (img != null && img.length > 0) {
			if (flyer == null) {
				flyer = new Flyer();
			}
			flyer.image = img;
			return flyer;
		} else {
			return null;
		}
	}

	@Override
	public IComponent getComponent() {
		return input;
	}
	
	private class ImageFieldChangeListener implements InputComponentListener {
		@Override
		public void changed(IComponent source) {
			// TODO image validation
			fireChange();
		}
	}
}