package outskirts.util.profiler;

import outskirts.util.StringUtils;

import java.util.*;

public class Profiler {

    private Section ROOT_SECTION = new Section("R");
    private Section currentSection = ROOT_SECTION;

    private boolean enable = true;

    public void push(String sectionName) {
        if (!enable)
            return;
        Section section = currentSection.getSub(sectionName);
        if (section == null) {
            section = new Section(sectionName);
            currentSection.addSub(section);
        }

        section.lastStartTimeNano = System.nanoTime();

        currentSection = section;
    }

    public Profiler pop() {
        if (!enable)
            return this;
        currentSection.calledCounter++;
        currentSection.lastUsedTimeNano = System.nanoTime() - currentSection.lastStartTimeNano;
        currentSection.totalUsedTimeNano+=currentSection.lastUsedTimeNano;

        currentSection = currentSection.parent;

        return this;
    }
    public final Profiler pop(String s) {
        return pop();
    }

    public Section getRootSection() {
////        // update root info
//        ROOT_SECTION.calledCounter=1;
//        ROOT_SECTION.lastUsedTimeNano = 0;
//        for (Section sub : ROOT_SECTION.subs) {
//            ROOT_SECTION.lastUsedTimeNano += sub.totalUsedTimeNano;
//        }
//        ROOT_SECTION.totalUsedTimeNano = ROOT_SECTION.lastUsedTimeNano;
        return ROOT_SECTION;
    }

    public void clearProfilerInfo(Section section) {
//        section.lastStartTimeNano = 0;
        section.totalUsedTimeNano = 0;
        section.lastUsedTimeNano = 0;
        section.calledCounter = 0;
        for (Section sub : section.subs) {
            clearProfilerInfo(sub);
        }
    }

    public Profiler setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public static final class Section {
        public final String name;
        private long lastStartTimeNano;
        public long lastUsedTimeNano;
        public long totalUsedTimeNano;
        public int calledCounter;
        public List<Section> subs = new ArrayList<>();
        public Section parent;

        private Section(String name) {
            this.name = name;
        }

        private void addSub(Section section) {
            section.parent = this;
            subs.add(section);
        }
        private Section getSub(String name) {
            for (Section sec : subs) {
                if (sec.name.equals(name))
                    return sec;
            }
            return null;
        }

        public Section getSection(String name) {
            if (name.startsWith(".")) name=name.substring(1);
            if (name.isEmpty()) return this;
            int endi = name.contains(".")?name.indexOf('.'):name.length();

            String subName = name.substring(0, endi);
            for (Section s : subs) {
                if (s.name.equals(subName)) {
                    return s.getSection(name.substring(endi));
                }
            }
            return null;
        }
        public String getFullName() {
            StringBuilder sb = new StringBuilder().insert(0, name);
            Section s=this;
            while ((s=s.parent)!=null) {
                sb.insert(0,'.').insert(0, s.name);
            }
            return sb.toString();
        }
    }
}
