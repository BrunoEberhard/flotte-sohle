package ch.openech.flottesohle.frontend;

import java.util.Arrays;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.PasswordFormElement;
import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.validation.Validation;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.util.CloneHelper;

import ch.openech.flottesohle.frontend.PasswordEditor.NewPassword;
import ch.openech.flottesohle.model.FlotteSohleUser;

public class PasswordEditor extends Editor<NewPassword, FlotteSohleUser> {

	private final FlotteSohleUser user;
	
	public PasswordEditor(FlotteSohleUser user) {
		this.user = user;
	}

	@Override
	public String getTitle() {
		return "Passwort für " + user.email;
	}
	
	@Override
	protected NewPassword createObject() {
		return new NewPassword();
	}

	@Override
	protected Form<NewPassword> createForm() {
		Form<NewPassword> form = new Form<>();
		form.line(new PasswordFormElement(NewPassword.$.line1));
		form.line(new PasswordFormElement(NewPassword.$.line2));
		return form;
	}

	@Override
	protected FlotteSohleUser save(NewPassword newPassword) {
		FlotteSohleUser changedUser = CloneHelper.clone(user);
		changedUser.password.setPassword(newPassword.line1);
		return Backend.save(changedUser);
	}
	
	public static class NewPassword implements Validation {
		public static final NewPassword $ = Keys.of(NewPassword.class);
		
		@Size(60)
		public char[] line1, line2;

		@Override
		public List<ValidationMessage> validate() {
			if (!Arrays.equals(line2, line1)) {
				return Validation.message($.line2, "Passwörter unterschiedlich");
			}
			return null;
		}
	}
	
	
}
