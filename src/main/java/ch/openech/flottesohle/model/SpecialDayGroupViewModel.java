package ch.openech.flottesohle.model;

import java.util.ArrayList;
import java.util.List;

import ch.openech.flottesohle.model.Location.SpecialDayInfo;

public class SpecialDayGroupViewModel {

	public SpecialDayGroupViewModel(String name) {
		this.name = name;
	}

	public final String name;

	public final List<SpecialDayInfo> specialDays = new ArrayList<>();

	public static List<SpecialDayGroupViewModel> toViewModel(Location location) {
		List<SpecialDayGroupViewModel> result = new ArrayList<>();

//		SpecialDayGroup group = null;
//		SpecialDayGroupViewModel groupViewModel = null;
//
//		List<SpecialDay> specialDays = Backend.find(SpecialDay.class, By.all().order(SpecialDay.$.date));
//		for (SpecialDay day : specialDays) {
//			if (day.group != group) {
//				group = day.group;
//				groupViewModel = new SpecialDayGroupViewModel(day.group.name());
//				result.add(groupViewModel);
//			}
//			Optional<SpecialDayInfo> info = location.specialDayInfos.stream().filter(i -> i.specialDay.id.equals(day.id)).findFirst();
//			groupViewModel.specialDays.add(info.orElseGet(() -> {
//				SpecialDayInfo i = new SpecialDayInfo();
//				i.specialDay = day;
//				i.closed = false;
//				return i;
//			}));
//		}

		return result;
	}

}
