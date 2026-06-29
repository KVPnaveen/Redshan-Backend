package com.redshanflora.redshanflora_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 0. Clean legacy wishlist constraints and tables to map to "user" table
        executeSafe("ALTER TABLE IF EXISTS wishlist DROP CONSTRAINT IF EXISTS fktrd6335blsefl2gxpb8lr0gr7");
        executeSafe("DROP TABLE IF EXISTS wishlist CASCADE");
        executeSafe("CREATE TABLE wishlist (" +
                "wishlist_id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES \"user\"(user_id) ON DELETE CASCADE" +
                ")");

        // 1. Seed Category Table
        executeSafe("INSERT INTO category (category_id, category_name, description) " +
                "VALUES (1, 'Bouquets', 'Beautiful handcrafted artificial flower bouquets for every occasion.') " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO category (category_id, category_name, description) " +
                "VALUES (2, 'Head Dresses', 'Elegant handmade artificial flower head dresses for weddings and events.') " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO category (category_id, category_name, description) " +
                "VALUES (3, 'Individual Flowers', 'Single artificial flowers including roses, lilies, orchids, and more.') " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO category (category_id, category_name, description) " +
                "VALUES (4, 'Wedding Collections', 'Premium artificial flower collections specially designed for weddings.') " +
                "ON CONFLICT (category_id) DO NOTHING");

        // 2. Seed SubCategory Table
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (20, 1, 'Anniversary') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (21, 1, 'Birthday') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (22, 1, 'Convocation') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (23, 2, 'Wedding') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (24, 2, 'Birthday') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (25, 3, 'Anniversary') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (26, 3, 'Birthday') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (27, 3, 'Convocation') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_category (sub_category_id, category_id, sub_category_name) " +
                "VALUES (9, 4, 'Table Arrangements') " +
                "ON CONFLICT (sub_category_id) DO NOTHING");

        // 3. Seed Product Table (Products 1 to 45)
        for (int id = 1; id <= 45; id++) {
            int categoryId = 1;
            String subName = "Anniversary";
            String name = "Product " + id;

            if (id <= 10) {
                categoryId = 1; // Bouquets
                if (id == 1) { name = "Pink Roses Bouquet"; subName = "Anniversary"; }
                else if (id == 2) { name = "Red Chrysanthemums"; categoryId = 3; subName = "Birthday"; }
                else if (id == 3) { name = "Purple Carnation"; categoryId = 3; subName = "Anniversary"; }
                else if (id == 4) { name = "Sakura Spring Bloom"; subName = "Birthday"; }
                else if (id == 5) { name = "Pink Chrysanthemums"; categoryId = 3; subName = "Birthday"; }
                else if (id == 6) { name = "Mauve Radiance Dahlia"; subName = "Convocation"; }
                else if (id == 7) { name = "Lily Headdress"; categoryId = 3; subName = "Birthday"; }
                else if (id == 8) { name = "Red Rose"; categoryId = 3; subName = "Anniversary"; }
                else if (id == 9) { name = "Faux Lavender Bundle"; categoryId = 3; subName = "Birthday"; }
                else if (id == 10) { name = "Pink Lily"; categoryId = 3; subName = "Birthday"; }
            } else if (id <= 23) {
                categoryId = 2; // Head Dresses
                subName = "Wedding";
                if (id == 14) name = "Rose Crown Head Dress";
                if (id == 15) name = "Pearl Blossom Floral Crown";
                if (id == 16) name = "Elegant Bridal Flower Tiara";
            } else {
                categoryId = 3; // Individual Flowers
                subName = "Convocation";
                if (id == 24) name = "Graduation Rose";
                if (id == 25) name = "Achievement Red Rose";
            }

            int subCategoryId = getSubCategoryId(categoryId, subName);
            double price = getProductPrice(id);

            executeSafe(String.format(
                "INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url) " +
                "VALUES (%d, %d, %d, '%s', 'Handcrafted premium artificial arrangement.', %.2f, 50, 0.0, 'product_%d.png') " +
                "ON CONFLICT (product_id) DO UPDATE SET price = EXCLUDED.price", id, categoryId, subCategoryId, name, price, id));
        }

        // 4. Adjust Sequences
        executeSafe("SELECT setval('product_product_id_seq', COALESCE((SELECT MAX(product_id)+1 FROM product), 1), false)");
        executeSafe("SELECT setval('category_category_id_seq', COALESCE((SELECT MAX(category_id)+1 FROM category), 1), false)");
        executeSafe("SELECT setval('sub_category_sub_category_id_seq', COALESCE((SELECT MAX(sub_category_id)+1 FROM sub_category), 1), false)");
    }

    private double getProductPrice(int id) {
        switch (id) {
            case 1: return 15000.00;
            case 2: return 11420.00;
            case 3: return 800.00;
            case 4: return 1500.00;
            case 5: return 2750.00;
            case 6: return 4110.00;
            case 7: return 3420.00;
            case 8: return 6500.00;
            case 9: return 3850.00;
            case 10: return 7800.00;
            case 11: return 9500.00;
            case 12: return 12000.00;
            case 13: return 8500.00;
            case 14: return 5500.00;
            case 15: return 6200.00;
            case 16: return 4800.00;
            case 17: return 7000.00;
            case 18: return 5900.00;
            case 19: return 3500.00;
            case 20: return 3800.00;
            case 21: return 4200.00;
            case 22: return 3200.00;
            case 23: return 3900.00;
            case 24: return 1200.00;
            case 25: return 1250.00;
            case 26: return 1400.00;
            case 27: return 1300.00;
            case 28: return 1500.00;
            case 29: return 1350.00;
            case 30: return 1100.00;
            case 31: return 1600.00;
            case 32: return 1250.00;
            case 33: return 1300.00;
            case 34: return 950.00;
            case 35: return 1000.00;
            case 36: return 1150.00;
            case 37: return 980.00;
            case 38: return 1050.00;
            default: return 1500.00;
        }
    }

    private int getSubCategoryId(int categoryId, String subCategoryName) {
        if (categoryId == 1) {
            if ("Anniversary".equalsIgnoreCase(subCategoryName)) return 20;
            if ("Birthday".equalsIgnoreCase(subCategoryName)) return 21;
            if ("Convocation".equalsIgnoreCase(subCategoryName)) return 22;
        } else if (categoryId == 2) {
            if ("Wedding".equalsIgnoreCase(subCategoryName)) return 23;
            if ("Birthday".equalsIgnoreCase(subCategoryName)) return 24;
        } else if (categoryId == 3) {
            if ("Anniversary".equalsIgnoreCase(subCategoryName)) return 25;
            if ("Birthday".equalsIgnoreCase(subCategoryName)) return 26;
            if ("Convocation".equalsIgnoreCase(subCategoryName)) return 27;
        } else if (categoryId == 4) {
            if ("Bridal Bouquets".equalsIgnoreCase(subCategoryName)) return 28;
            if ("Reception Decorations".equalsIgnoreCase(subCategoryName)) return 29;
            if ("Bridesmaid Flowers".equalsIgnoreCase(subCategoryName)) return 30;
            if ("Table Arrangements".equalsIgnoreCase(subCategoryName)) return 9;
        }
        return 20; // Default fallback
    }

    private void executeSafe(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.out.println("DataSeeder Statement Ignored: " + e.getMessage());
        }
    }
}
