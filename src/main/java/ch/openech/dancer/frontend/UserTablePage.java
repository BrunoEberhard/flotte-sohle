package ch.openech.dancer.frontend;

import java.util.List;
import java.util.Objects;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.CheckBoxFormElement;
import org.minimalj.frontend.form.element.CheckBoxFormElement.CheckBoxProperty;
import org.minimalj.frontend.form.element.PasswordFormElement;
import org.minimalj.frontend.form.element.TextFormElement;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.EnumUtils;
import org.minimalj.repository.query.By;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserRole;

import ch.openech.dancer.DancerRoles;

public class UserTablePage extends SimpleTableEditorPage<User> {

	private static final Object[] keys = new Object[] { User.$.name };
	
	public UserTablePage() {
		super(keys);
	}
	
	@Override
	protected List<User> load() {
		return Backend.find(User.class, By.all());
	}

	@Override
	public void action(User user) {
		// don't show detail on double click just open editor
		openEditor(user);
	}
	
	@Override
	protected Form<User> createForm(boolean editable, boolean newObject) {
		Form<User> form = new Form<>(editable, 2);
		if (editable && newObject) {
			form.line(User.$.name);
			form.line(new PasswordFormElement(User.$.password));
		} else {
			form.line(new TextFormElement(User.$.name));
		}
		form.text("Rollen");
		for (DancerRoles r : DancerRoles.values()) {
			form.lineWithoutCaption(new CheckBoxFormElement(new EnumSetFormElementProperty(r), EnumUtils.getText(r), true));
		}
		return form;
	}
	
	private class EnumSetFormElementProperty extends CheckBoxProperty {
		private final DancerRoles r;

		public EnumSetFormElementProperty(DancerRoles r) {
			this.r = Objects.requireNonNull(r);
		}

		@Override
		public Class<?> getDeclaringClass() {
			return User.class;
		}

		@Override
		public String getName() {
			return r.name();
		}

		@Override
		public String getPath() {
			return r.name();
		}

		@Override
		public Class<?> getClazz() {
			return User.class;
		}

		@Override
		public Boolean getValue(Object object) {
			User user = (User) object;
			return user.roles.stream().anyMatch(userRole -> r.name().equals(userRole.name));
		}

		@Override
		public void setValue(Object object, Object newValue) {
			User user = (User) object;
			if (Boolean.TRUE.equals(newValue)) {
				if (!getValue(object)) {
					user.roles.add(new UserRole(r.name()));
				}
			} else {
				user.roles.removeIf(userRole -> r.name().equals(userRole.name));
			}
		}
	}
	
}
