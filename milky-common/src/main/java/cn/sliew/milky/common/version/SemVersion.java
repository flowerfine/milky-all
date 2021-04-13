package cn.sliew.milky.common.version;

/**
 * semantic versioning implementions.
 *
 * @see <a href="http://semver.org" />
 */
public class SemVersion {

    private final int major;
    private final int minor;
    private final int patch;

    public SemVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public SemVersion upMajor() {
        return SemVersion.semVersion(this.major + 1, this.minor, this.patch);
    }

    public SemVersion upMinor() {
        return SemVersion.semVersion(this.major, this.minor + 1, this.patch);
    }

    public SemVersion upPatch() {
        return SemVersion.semVersion(this.major, this.minor, this.patch + 1);
    }

    public static SemVersion semVersion() {
        return new SemVersion(0, 0, 1);
    }

    public static SemVersion semVersion(int major, int minor, int patch) {
        if ((major == 0 && minor == 0 && patch == 0) || (major < 0 || minor < 0 || patch < 0)) {
            throw new IllegalArgumentException(String.format("bad semantic version %d.%d.%d must not be negative", major, minor, patch));
        }
        return new SemVersion(major, minor, patch);
    }

    public static SemVersion semVersion(String version) {
        try {
            String[] parts = new String[0];
            if (version != null && !version.isEmpty()) {
                parts = version.split("\\.");
            }

            int major = 0;
            if (parts.length >= 1) {
                major = Integer.parseInt(parts[0]);
            }
            int minor = 0;
            if (parts.length >= 2) {
                minor = Integer.parseInt(parts[1]);
            }
            int patch = 0;
            if (parts.length >= 3) {
                patch = Integer.parseInt(parts[2]);
            }
            return semVersion(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("bad semantic version %s", version));
        }
    }


    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    public static void main(String[] args) {
        System.out.println(SemVersion.semVersion(1, 1, -1));
    }


}
