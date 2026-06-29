package com.redshanflora.redshanflora_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private  final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Categories
        executeSafe("INSERT INTO categories (category_id, category_name, description, image_url, created_at) " +
                "VALUES (1, 'Bouquets', 'Beautiful handcrafted artificial flower bouquets for every occasion.', 'bouquets.jpg', NOW()) " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO categories (category_id, category_name, description, image_url, created_at) " +
                "VALUES (2, 'Head Dresses', 'Elegant handmade artificial flower head dresses for weddings and events.', 'head_dresses.jpg', NOW()) " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO categories (category_id, category_name, description, image_url, created_at) " +
                "VALUES (3, 'Individual Flowers', 'Single artificial flowers including roses, lilies, orchids, and more.', 'individual_flowers.jpg', NOW()) " +
                "ON CONFLICT (category_id) DO NOTHING");
        executeSafe("INSERT INTO categories (category_id, category_name, description, image_url, created_at) " +
                "VALUES (4, 'Wedding Collections', 'Premium artificial flower collections specially designed for weddings.', 'wedding_collections.jpg', NOW()) " +
                "ON CONFLICT (category_id) DO NOTHING");

        // 2. Seed Subcategories (We use try-catch safely, and map to existing subcategories if they exist)
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (20, 1, 'Anniversary', 'Anniversary bouquets', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (21, 1, 'Birthday', 'Birthday bouquets', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (22, 1, 'Convocation', 'Convocation bouquets', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (23, 2, 'Wedding', 'Wedding head dresses', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (24, 2, 'Birthday', 'Birthday head dresses', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (25, 3, 'Anniversary', 'Anniversary flowers', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (26, 3, 'Birthday', 'Birthday flowers', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (27, 3, 'Convocation', 'Convocation flowers', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");
        executeSafe("INSERT INTO sub_categories (sub_category_id, category_id, sub_category_name, description, created_at) " +
                "VALUES (9, 4, 'Table Arrangements', 'Table Arrangements', NOW()) " +
                "ON CONFLICT (sub_category_id) DO NOTHING");

        // 3. Seed Products 1 to 45
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

            executeSafe(String.format(
                "INSERT INTO products (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, image_url, view_360_url, discount_percentage, featured, status, created_at, updated_at) " +
                "VALUES (%d, %d, %d, '%s', 'Handcrafted premium artificial arrangement.', 1500.00, 50, 'product_%d.png', '', 0.0, true, 'ACTIVE', NOW(), NOW()) " +
                "ON CONFLICT (product_id) DO NOTHING", id, categoryId, subCategoryId, name, id));
        }

        // 4. Adjust PostgreSQL sequences
        executeSafe("SELECT setval('products_product_id_seq', COALESCE((SELECT MAX(product_id)+1 FROM products), 1), false)");
        executeSafe("SELECT setval('categories_category_id_seq', COALESCE((SELECT MAX(category_id)+1 FROM categories), 1), false)");
        executeSafe("SELECT setval('sub_categories_sub_category_id_seq', COALESCE((SELECT MAX(sub_category_id)+1 FROM sub_categories), 1), false)");
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
