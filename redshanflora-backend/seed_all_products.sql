-- Product 1: Pink Roses Bouquet
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Pink Roses Bouquet',
    description = 'Crafted with surgical precision from the finest premium silk, our Everlasting Peony captures the ethereal beauty of nature without the impermanence. Featuring hand-painted details and flexible architectural stems.',
    price = 15000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pink_roses_bouquet.png'
WHERE product_id = 1;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 1, 1, 2, 'Pink Roses Bouquet', 'Crafted with surgical precision from the finest premium silk, our Everlasting Peony captures the ethereal beauty of nature without the impermanence. Featuring hand-painted details and flexible architectural stems.', 15000.00, 100, 0.00, 'pink_roses_bouquet.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 1);

-- Product 101: Product 101
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 101',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 101;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 101, 1, 2, 'Product 101', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 101);

-- Product 102: Product 102
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 102',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 102;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 102, 1, 2, 'Product 102', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 102);

-- Product 2: Red Chrysanthemums
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Red Chrysanthemums',
    description = 'An exquisite arrangement of velvet-textured tulips, hand-stitched for an elegant velvet finish. Designed to reflect soft moonlight in high-end spaces.',
    price = 11420.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'red_chrysanthemum.png'
WHERE product_id = 2;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 2, 1, 1, 'Red Chrysanthemums', 'An exquisite arrangement of velvet-textured tulips, hand-stitched for an elegant velvet finish. Designed to reflect soft moonlight in high-end spaces.', 11420.00, 100, 0.00, 'red_chrysanthemum.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 2);

-- Product 201: Product 201
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 201',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 201;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 201, 1, 2, 'Product 201', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 201);

-- Product 3: Purple Carnation
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Purple Carnation',
    description = 'A whimsical cloud of natural dried pampas grass and preserved botanicals, styled in an organic, rustic-modern fashion. Perfect for boho-chic decor.',
    price = 800.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'purple_carnation.png'
WHERE product_id = 3;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 3, 1, 1, 'Purple Carnation', 'A whimsical cloud of natural dried pampas grass and preserved botanicals, styled in an organic, rustic-modern fashion. Perfect for boho-chic decor.', 800.00, 100, 0.00, 'purple_carnation.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 3);

-- Product 301: Product 301
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 301',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 301;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 301, 1, 2, 'Product 301', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 301);

-- Product 4: Sakura Spring Bloom
UPDATE product SET
    category_id = 2,
    sub_category_id = 4,
    product_name = 'Sakura Spring Bloom',
    description = 'Inspired by Japanese cherry blossoms, these delicate silk stems bring a fresh, modern touch of spring into your home year-round. Featuring flexible branches.',
    price = 1500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'con2.png'
WHERE product_id = 4;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 4, 2, 4, 'Sakura Spring Bloom', 'Inspired by Japanese cherry blossoms, these delicate silk stems bring a fresh, modern touch of spring into your home year-round. Featuring flexible branches.', 1500.00, 100, 0.00, 'con2.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 4);

-- Product 401: Product 401
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 401',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 401;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 401, 1, 2, 'Product 401', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 401);

-- Product 5: Pink Chrysanthemums
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Pink Chrysanthemums',
    description = 'An exotic protea bloom with structural leaves and a bold burgundy color palette, making it a dramatic centerpiece for minimalist styling.',
    price = 2750.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pink_chrysanthemum.png'
WHERE product_id = 5;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 5, 1, 1, 'Pink Chrysanthemums', 'An exotic protea bloom with structural leaves and a bold burgundy color palette, making it a dramatic centerpiece for minimalist styling.', 2750.00, 100, 0.00, 'pink_chrysanthemum.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 5);

-- Product 501: Product 501
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 501',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 501;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 501, 1, 2, 'Product 501', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 501);

-- Product 6: Mauve Radiance Dahlia
UPDATE product SET
    category_id = 3,
    sub_category_id = 7,
    product_name = 'Mauve Radiance Dahlia',
    description = 'A rich bouquet of velvet dahlias in dusty mauve tones. Perfect for adding a warm, sophisticated pop of color to neutral spaces.',
    price = 4110.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'mauve_radiance_dahlia.png'
WHERE product_id = 6;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 6, 3, 7, 'Mauve Radiance Dahlia', 'A rich bouquet of velvet dahlias in dusty mauve tones. Perfect for adding a warm, sophisticated pop of color to neutral spaces.', 4110.00, 100, 0.00, 'mauve_radiance_dahlia.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 6);

-- Product 601: Product 601
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 601',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 601;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 601, 1, 2, 'Product 601', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 601);

-- Product 7: Lily Headdress
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Lily Headdress',
    description = 'A minimalist white orchid stem presented in a modern matte black ceramic pot. Truly timeless and elegant.',
    price = 3420.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'lily_headdress.jpg'
WHERE product_id = 7;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 7, 1, 2, 'Lily Headdress', 'A minimalist white orchid stem presented in a modern matte black ceramic pot. Truly timeless and elegant.', 3420.00, 100, 0.00, 'lily_headdress.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 7);

-- Product 701: Product 701
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 701',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 701;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 701, 1, 2, 'Product 701', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 701);

-- Product 8: Red Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Red Rose',
    description = 'Three long-stemmed velvet roses in a rich crimson hue, meticulously detailed to represent real fresh-cut roses.',
    price = 6500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'red_rose.png'
WHERE product_id = 8;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 8, 1, 1, 'Red Rose', 'Three long-stemmed velvet roses in a rich crimson hue, meticulously detailed to represent real fresh-cut roses.', 6500.00, 100, 0.00, 'red_rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 8);

-- Product 801: Product 801
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 801',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 801;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 801, 1, 2, 'Product 801', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 801);

-- Product 9: Faux Lavender Bundle
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Faux Lavender Bundle',
    description = 'A rustic bundle of French lavender, coated with a light dusting of powder to perfectly mimic natural lavender blooms.',
    price = 3850.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'wild_hearth.jpg'
WHERE product_id = 9;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 9, 1, 2, 'Faux Lavender Bundle', 'A rustic bundle of French lavender, coated with a light dusting of powder to perfectly mimic natural lavender blooms.', 3850.00, 100, 0.00, 'wild_hearth.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 9);

-- Product 901: Product 901
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 901',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 901;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 901, 1, 2, 'Product 901', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 901);

-- Product 10: Pink Lily
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Pink Lily',
    description = 'An impressive single-stem magnolia bloom with thick, waxy white petals and realistic woody branch detailing.',
    price = 7800.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pink_lily.jpg'
WHERE product_id = 10;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 10, 1, 1, 'Pink Lily', 'An impressive single-stem magnolia bloom with thick, waxy white petals and realistic woody branch detailing.', 7800.00, 100, 0.00, 'pink_lily.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 10);

-- Product 1001: Product 1001
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Product 1001',
    description = '',
    price = 5000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = '.png'
WHERE product_id = 1001;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 1001, 1, 2, 'Product 1001', '', 5000.00, 100, 0.00, '.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 1001);

-- Product 11: Red Romance Bouquet
UPDATE product SET
    category_id = 1,
    sub_category_id = 2,
    product_name = 'Red Romance Bouquet',
    description = 'An elegant arrangement of crimson red roses crafted from premium velvet, perfectly arranged to express timeless love and romance.',
    price = 9500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'red_rose.png'
WHERE product_id = 11;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 11, 1, 2, 'Red Romance Bouquet', 'An elegant arrangement of crimson red roses crafted from premium velvet, perfectly arranged to express timeless love and romance.', 9500.00, 100, 0.00, 'red_rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 11);

-- Product 12: Golden Celebration Bouquet
UPDATE product SET
    category_id = 2,
    sub_category_id = 4,
    product_name = 'Golden Celebration Bouquet',
    description = 'A cheerful celebration arrangement combining hand-painted silk flowers in bright gold and pink tones, ideal for birthdays and happy occasions.',
    price = 12000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pink_roses_bouquet.png'
WHERE product_id = 12;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 12, 2, 4, 'Golden Celebration Bouquet', 'A cheerful celebration arrangement combining hand-painted silk flowers in bright gold and pink tones, ideal for birthdays and happy occasions.', 12000.00, 100, 0.00, 'pink_roses_bouquet.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 12);

-- Product 13: Graduation Glory Bouquet
UPDATE product SET
    category_id = 3,
    sub_category_id = 7,
    product_name = 'Graduation Glory Bouquet',
    description = 'A stately bouquet of pure white orchids and lilies symbolizing honor, success, and high achievements. Perfect for graduation ceremonies.',
    price = 8500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'con2.png'
WHERE product_id = 13;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 13, 3, 7, 'Graduation Glory Bouquet', 'A stately bouquet of pure white orchids and lilies symbolizing honor, success, and high achievements. Perfect for graduation ceremonies.', 8500.00, 100, 0.00, 'con2.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 13);

-- Product 14: Rose Crown Head Dress
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Rose Crown Head Dress',
    description = 'A beautiful luxury rose crown handcrafted from premium silk roses. Designed to add majestic bridal elegance on your dream wedding day.',
    price = 5500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'rose-crown-head-dress.png'
WHERE product_id = 14;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 14, 2, 5, 'Rose Crown Head Dress', 'A beautiful luxury rose crown handcrafted from premium silk roses. Designed to add majestic bridal elegance on your dream wedding day.', 5500.00, 100, 0.00, 'rose-crown-head-dress.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 14);

-- Product 15: Pearl Blossom Floral Crown
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Pearl Blossom Floral Crown',
    description = 'An elegant floral crown featuring premium white blossoms and delicate pearl accents, bringing graceful style to bridal ensembles.',
    price = 6200.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pearl-blossom-floral-crown.png'
WHERE product_id = 15;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 15, 2, 5, 'Pearl Blossom Floral Crown', 'An elegant floral crown featuring premium white blossoms and delicate pearl accents, bringing graceful style to bridal ensembles.', 6200.00, 100, 0.00, 'pearl-blossom-floral-crown.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 15);

-- Product 16: Elegant Bridal Flower Tiara
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Elegant Bridal Flower Tiara',
    description = 'A gorgeous wedding tiara crafted with cream rose buds and delicate baby\',
    price = 4800.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'bridal-flower-tiara.png'
WHERE product_id = 16;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 16, 2, 5, 'Elegant Bridal Flower Tiara', 'A gorgeous wedding tiara crafted with cream rose buds and delicate baby\', 4800.00, 100, 0.00, 'bridal-flower-tiara.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 16);

-- Product 17: White Orchid Wedding Crown
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'White Orchid Wedding Crown',
    description = 'A stately head dress of hyper-realistic white orchid blooms and fresh green leaves, perfect for destination or garden weddings.',
    price = 7000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'white-orchid-wedding-crown.png'
WHERE product_id = 17;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 17, 2, 5, 'White Orchid Wedding Crown', 'A stately head dress of hyper-realistic white orchid blooms and fresh green leaves, perfect for destination or garden weddings.', 7000.00, 100, 0.00, 'white-orchid-wedding-crown.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 17);

-- Product 18: Luxury Wedding Flower Halo
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Luxury Wedding Flower Halo',
    description = 'A romantic floral halo crown in soft pink and cream tones, evoking an ethereal garden vibe for brides and bridesmaids.',
    price = 5900.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'luxury-wedding-flower-halo.png'
WHERE product_id = 18;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 18, 2, 5, 'Luxury Wedding Flower Halo', 'A romantic floral halo crown in soft pink and cream tones, evoking an ethereal garden vibe for brides and bridesmaids.', 5900.00, 100, 0.00, 'luxury-wedding-flower-halo.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 18);

-- Product 19: Pink Celebration Floral Crown
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Pink Celebration Floral Crown',
    description = 'A vibrant celebration floral crown featuring bright pink silk roses and pastel ranunculus, perfect for birthday parties and celebrations.',
    price = 3500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pink-celebration-floral-crown.png'
WHERE product_id = 19;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 19, 2, 5, 'Pink Celebration Floral Crown', 'A vibrant celebration floral crown featuring bright pink silk roses and pastel ranunculus, perfect for birthday parties and celebrations.', 3500.00, 100, 0.00, 'pink-celebration-floral-crown.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 19);

-- Product 20: Rainbow Blossom Head Dress
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Rainbow Blossom Head Dress',
    description = 'A playful festival-style head dress with colorful pastel blossoms and cascading ribbons, perfect for a cheerful birthday celebration.',
    price = 3800.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'rainbow-blossom-head-dress.png'
WHERE product_id = 20;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 20, 2, 5, 'Rainbow Blossom Head Dress', 'A playful festival-style head dress with colorful pastel blossoms and cascading ribbons, perfect for a cheerful birthday celebration.', 3800.00, 100, 0.00, 'rainbow-blossom-head-dress.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 20);

-- Product 21: Birthday Princess Flower Crown
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Birthday Princess Flower Crown',
    description = 'An elegant princess tiara crown made of lavender and cream roses, adorned with subtle golden stars to celebrate your special birthday.',
    price = 4200.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'birthday-princess-crown.png'
WHERE product_id = 21;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 21, 2, 5, 'Birthday Princess Flower Crown', 'An elegant princess tiara crown made of lavender and cream roses, adorned with subtle golden stars to celebrate your special birthday.', 4200.00, 100, 0.00, 'birthday-princess-crown.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 21);

-- Product 22: Pastel Garden Floral Headband
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Pastel Garden Floral Headband',
    description = 'A delicate floral headband featuring pastel green, yellow and pink flowers, ideal for comfortable wear during outdoor birthday garden parties.',
    price = 3200.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'pastel-garden-floral-headband.png'
WHERE product_id = 22;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 22, 2, 5, 'Pastel Garden Floral Headband', 'A delicate floral headband featuring pastel green, yellow and pink flowers, ideal for comfortable wear during outdoor birthday garden parties.', 3200.00, 100, 0.00, 'pastel-garden-floral-headband.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 22);

-- Product 23: Floral Party Head Dress
UPDATE product SET
    category_id = 2,
    sub_category_id = 5,
    product_name = 'Floral Party Head Dress',
    description = 'A beautiful head dress boasting vibrant lilac, soft cream, and rose gold blossoms, adding a glamorous floral finish to any party outfit.',
    price = 3900.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'floral-party-head-dress.png'
WHERE product_id = 23;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 23, 2, 5, 'Floral Party Head Dress', 'A beautiful head dress boasting vibrant lilac, soft cream, and rose gold blossoms, adding a glamorous floral finish to any party outfit.', 3900.00, 100, 0.00, 'floral-party-head-dress.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 23);

-- Product 24: Graduation Rose
UPDATE product SET
    category_id = 3,
    sub_category_id = 6,
    product_name = 'Graduation Rose',
    description = 'A beautiful handmade artificial graduation rose crafted from premium silk. The perfect symbolic single-stem gift to celebrate high achievements.',
    price = 1200.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'graduation-rose.png'
WHERE product_id = 24;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 24, 3, 6, 'Graduation Rose', 'A beautiful handmade artificial graduation rose crafted from premium silk. The perfect symbolic single-stem gift to celebrate high achievements.', 1200.00, 100, 0.00, 'graduation-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 24);

-- Product 25: Achievement Red Rose
UPDATE product SET
    category_id = 3,
    sub_category_id = 6,
    product_name = 'Achievement Red Rose',
    description = 'A rich red silk rose symbolizing success, dedication, and honor. An excellent convocation gift for graduates.',
    price = 1250.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'achievement-red-rose.png'
WHERE product_id = 25;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 25, 3, 6, 'Achievement Red Rose', 'A rich red silk rose symbolizing success, dedication, and honor. An excellent convocation gift for graduates.', 1250.00, 100, 0.00, 'achievement-red-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 25);

-- Product 26: Success Gold Rose
UPDATE product SET
    category_id = 3,
    sub_category_id = 6,
    product_name = 'Success Gold Rose',
    description = 'An opulent hand-painted golden rose representing brilliant success and achievement. Perfect for graduation celebrations.',
    price = 1400.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'success-gold-rose.png'
WHERE product_id = 26;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 26, 3, 6, 'Success Gold Rose', 'An opulent hand-painted golden rose representing brilliant success and achievement. Perfect for graduation celebrations.', 1400.00, 100, 0.00, 'success-gold-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 26);

-- Product 27: Graduation Blue Rose
UPDATE product SET
    category_id = 3,
    sub_category_id = 6,
    product_name = 'Graduation Blue Rose',
    description = 'A unique royal blue artificial rose representing distinction and wisdom, styled with elegant ribbons.',
    price = 1300.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'graduation-blue-rose.png'
WHERE product_id = 27;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 27, 3, 6, 'Graduation Blue Rose', 'A unique royal blue artificial rose representing distinction and wisdom, styled with elegant ribbons.', 1300.00, 100, 0.00, 'graduation-blue-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 27);

-- Product 28: Convocation Celebration Rose
UPDATE product SET
    category_id = 3,
    sub_category_id = 6,
    product_name = 'Convocation Celebration Rose',
    description = 'A stately celebration single-stem flower arrangement presented with a miniature graduation ribbon loop.',
    price = 1500.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IBI1.jpg'
WHERE product_id = 28;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 28, 3, 6, 'Convocation Celebration Rose', 'A stately celebration single-stem flower arrangement presented with a miniature graduation ribbon loop.', 1500.00, 100, 0.00, 'IBI1.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 28);

-- Product 29: Eternal Love Red Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Eternal Love Red Rose',
    description = 'An elegant long-stemmed crimson red silk rose representing undying romance. A timeless choice for anniversaries.',
    price = 1350.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'eternal-love-red-rose.png'
WHERE product_id = 29;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 29, 1, 1, 'Eternal Love Red Rose', 'An elegant long-stemmed crimson red silk rose representing undying romance. A timeless choice for anniversaries.', 1350.00, 100, 0.00, 'eternal-love-red-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 29);

-- Product 30: Romantic Silk Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Romantic Silk Rose',
    description = 'A soft blush pink silk rose capturing the delicate beauty of fresh blossoms. Perfect for romantic expressions.',
    price = 1100.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IAI5.jpg'
WHERE product_id = 30;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 30, 1, 1, 'Romantic Silk Rose', 'A soft blush pink silk rose capturing the delicate beauty of fresh blossoms. Perfect for romantic expressions.', 1100.00, 100, 0.00, 'IAI5.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 30);

-- Product 31: Golden Anniversary Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Golden Anniversary Rose',
    description = 'A majestic dahlia-shaped golden rose celebrating precious milestones and years of shared love.',
    price = 1600.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'golden-anniversary-rose.png'
WHERE product_id = 31;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 31, 1, 1, 'Golden Anniversary Rose', 'A majestic dahlia-shaped golden rose celebrating precious milestones and years of shared love.', 1600.00, 100, 0.00, 'golden-anniversary-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 31);

-- Product 32: Luxury White Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Luxury White Rose',
    description = 'A pure white silk rose symbolizing devotion, loyalty, and pure everlasting love.',
    price = 1250.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IAI6.jpg'
WHERE product_id = 32;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 32, 1, 1, 'Luxury White Rose', 'A pure white silk rose symbolizing devotion, loyalty, and pure everlasting love.', 1250.00, 100, 0.00, 'IAI6.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 32);

-- Product 33: Forever Bloom Rose
UPDATE product SET
    category_id = 1,
    sub_category_id = 1,
    product_name = 'Forever Bloom Rose',
    description = 'A hyper-realistic pink lily-rose stem designed to retain its elegant beauty forever.',
    price = 1300.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'forever-bloom-artificial-rose.png'
WHERE product_id = 33;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 33, 1, 1, 'Forever Bloom Rose', 'A hyper-realistic pink lily-rose stem designed to retain its elegant beauty forever.', 1300.00, 100, 0.00, 'forever-bloom-artificial-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 33);

-- Product 34: Birthday Celebration Rose
UPDATE product SET
    category_id = 2,
    sub_category_id = 3,
    product_name = 'Birthday Celebration Rose',
    description = 'A bright pink celebration rose, perfect for adding joyful color and cheer to birthday gifts.',
    price = 950.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'birthday-celebration-rose.png'
WHERE product_id = 34;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 34, 2, 3, 'Birthday Celebration Rose', 'A bright pink celebration rose, perfect for adding joyful color and cheer to birthday gifts.', 950.00, 100, 0.00, 'birthday-celebration-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 34);

-- Product 35: Pink Blossom Rose
UPDATE product SET
    category_id = 2,
    sub_category_id = 3,
    product_name = 'Pink Blossom Rose',
    description = 'A charming pink blossom stem that brings a fresh garden feel to birthday arrangements.',
    price = 1000.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IBI4.jpg'
WHERE product_id = 35;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 35, 2, 3, 'Pink Blossom Rose', 'A charming pink blossom stem that brings a fresh garden feel to birthday arrangements.', 1000.00, 100, 0.00, 'IBI4.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 35);

-- Product 36: Rainbow Silk Rose
UPDATE product SET
    category_id = 2,
    sub_category_id = 3,
    product_name = 'Rainbow Silk Rose',
    description = 'A multicolored single-stem flower designed to bring whimsical fun and color to birthdays.',
    price = 1150.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'rainbow-silk-rose.png'
WHERE product_id = 36;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 36, 2, 3, 'Rainbow Silk Rose', 'A multicolored single-stem flower designed to bring whimsical fun and color to birthdays.', 1150.00, 100, 0.00, 'rainbow-silk-rose.png'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 36);

-- Product 37: Cheerful Yellow Rose
UPDATE product SET
    category_id = 2,
    sub_category_id = 3,
    product_name = 'Cheerful Yellow Rose',
    description = 'A bright yellow silk rose that captures the essence of sunshine, symbolizing friendship and birthday joy.',
    price = 980.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IBI2.jpg'
WHERE product_id = 37;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 37, 2, 3, 'Cheerful Yellow Rose', 'A bright yellow silk rose that captures the essence of sunshine, symbolizing friendship and birthday joy.', 980.00, 100, 0.00, 'IBI2.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 37);

-- Product 38: Birthday Floral Gift Rose
UPDATE product SET
    category_id = 2,
    sub_category_id = 3,
    product_name = 'Birthday Floral Gift Rose',
    description = 'A charming single-stem purple blossom featuring a matching card holder, designed as a ready-to-give birthday gift.',
    price = 1050.00,
    stock_quantity = 100,
    discount_percentage = 0.00,
    image_url = 'IBI3.jpg'
WHERE product_id = 38;

INSERT INTO product (product_id, category_id, sub_category_id, product_name, description, price, stock_quantity, discount_percentage, image_url)
SELECT 38, 2, 3, 'Birthday Floral Gift Rose', 'A charming single-stem purple blossom featuring a matching card holder, designed as a ready-to-give birthday gift.', 1050.00, 100, 0.00, 'IBI3.jpg'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_id = 38);

SELECT setval(pg_get_serial_sequence('product', 'product_id'), COALESCE((SELECT MAX(product_id) FROM product), 0) + 1, false);
