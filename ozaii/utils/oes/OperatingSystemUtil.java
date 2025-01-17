package ozaii.utils.oes;

public enum OperatingSystemUtil {

    WINDOWS("Windows"),
    LINUX("Linux"),
    MACOS("macOS"),
    UNKNOWN("Unknown");

    private final String name;

    // Enum yapıcısı
    OperatingSystemUtil(String name) {
        this.name = name;
    }

    // İşletim sistemi adını döndürür
    public String getName() {
        return name;
    }

    // Mevcut işletim sistemini belirler
    public static OperatingSystemUtil detectCurrentOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return LINUX;
        } else if (osName.contains("mac")) {
            return MACOS;
        } else {
            return UNKNOWN;
        }
    }

    // İşletim sistemi seçici
    public static void handleOperatingSystem(OperatingSystemUtil os) {
        switch (os) {
            case WINDOWS:
                System.out.println("Windows işletim sistemi seçildi.");
                // Windows'a özel işlemler
                break;
            case LINUX:
                System.out.println("Linux işletim sistemi seçildi.");
                // Linux'a özel işlemler
                break;
            case MACOS:
                System.out.println("macOS işletim sistemi seçildi.");
                // macOS'a özel işlemler
                break;
            case UNKNOWN:
                System.out.println("Bilinmeyen bir işletim sistemi algılandı.");
                // Bilinmeyen sistemler için işlemler
                break;
            default:
                throw new IllegalStateException("Bilinmeyen işletim sistemi: " + os);
        }
    }

}
