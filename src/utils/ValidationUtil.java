package utils;

public class ValidationUtil {
    public static boolean isValidName(String name) throws ValidationException {
        if (name == null || !name.matches("^[\\p{L} ]{2,}$")) {
            throw new ValidationException(name + " is not a valid name");
        }
        return true;
    }

    public static boolean isValidEmail(String email) throws ValidationException {

        if (email == null || !email.matches("^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$")) {
            throw new ValidationException(email + " is not a valid email");
        }
        return true;
    }

    public static boolean isValidEgyptianPhone(String phone) throws ValidationException {
        if (phone == null || !phone.matches("^01[0-2,5]{1}\\d{8}$")) {
            throw new ValidationException(phone + " is not a valid Egyptian phone number");
        }
        return true;
    }

    public static boolean isValidLicenseNumber(String licenseNumber) throws ValidationException {
        if (licenseNumber == null || !licenseNumber.matches("^\\d{6,15}$")) {
            throw new ValidationException(licenseNumber + " is not a valid license number");
        }
        return true;
    }

    public static boolean isNumeric(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Input is null or empty and not numeric");
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            throw new ValidationException(input + " is not numeric");
        }
    }

    public static boolean isValidPassword(String password) throws ValidationException {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_*()\\-]).{8,}$")) {
            throw new ValidationException(
                    "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a digit, and a special character (@#$%^&+=!_*()-etc).");
        }
        return true;
    }

    public static boolean isValidDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty.");
        }
        return true;
    }

    public static boolean isValidURL(String url) throws ValidationException {
        if (url == null || !url.matches("^(http|https)://.*$")) {
            throw new ValidationException(url + " is not a valid URL");
        }
        return true;
    }

    // Validate that discount percentage is numeric and <= 100
    public static boolean isValidDiscountPercentage(String percentage) throws ValidationException {
        isValidFloat(percentage); // Validate that it is a valid float
        float value = Float.parseFloat(percentage);
        if (value < 0 || value > 100) {
            throw new ValidationException("Discount percentage must be between 0 and 100.");
        }
        return true;
    }

    // Validate that promotion code is alphanumeric and max 30 characters
    public static boolean isValidPromotionCode(String promotionCode) throws ValidationException {
        if (promotionCode == null || promotionCode.trim().isEmpty()) {
            throw new ValidationException("Promotion code cannot be empty.");
        }
        if (!promotionCode.matches("^[a-zA-Z0-9]{1,30}$")) {
            throw new ValidationException("Promotion code must be alphanumeric and up to 30 characters.");
        }
        return true;
    }

    public static boolean isValidFloat(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Input is null or empty and not a valid float");
        }
        try {
            Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            throw new ValidationException(input + " is not a valid float number");
        }
    }
}