package com.nicobrailo.pianoli;

class Piano {
    class Key {
        int x_i, x_f, y_i, y_f;

        Key(int x_i, int x_f, int y_i, int y_f) {
            this.x_i = x_i;
            this.x_f = x_f;
            this.y_i = y_i;
            this.y_f = y_f;
        }

        boolean contains(float pos_x, float pos_y) {
            return (pos_x > x_i && pos_x < x_f) &&
                    (pos_y > y_i && pos_y < y_f);
        }
    }

    private static final double KEYS_FLAT_HEIGHT_RATIO = 0.55;
    private static final int KEYS_WIDTH = 220;
    private static final int KEYS_FLAT_WIDTH = 130;

    private final int keys_height;
    private final int keys_flats_height;

    private final int keys_count;
    private boolean key_pressed[];

    Piano(int screen_size_x, int screen_size_y) {
        keys_height = screen_size_y;
        keys_flats_height = (int) (screen_size_y * KEYS_FLAT_HEIGHT_RATIO);

        // Round up for possible half-key display
        final int big_keys = 1 + (screen_size_x / KEYS_WIDTH);
        // Count flats too
        keys_count = big_keys * 2;

        key_pressed = new boolean[keys_count];
        for (int i = 0; i < key_pressed.length; ++i) key_pressed[i] = false;
    }

    public int get_keys_count() {
        return keys_count;
    }

    public boolean is_key_pressed(int key_idx) {
        return key_pressed[key_idx];
    }

    public void on_key_down(int key_idx) {
        key_pressed[key_idx] = true;
    }

    public void on_key_up(int key_idx) {
        key_pressed[key_idx] = false;
    }

    public int pos_to_key_idx(float pos_x, float pos_y) {
        final int big_key_idx = 2 * ((int) pos_x / KEYS_WIDTH);
        if (pos_y > keys_flats_height) return big_key_idx;

        // Check if press is inside rect of flat key
        Key flat = get_area_for_flat_key(big_key_idx);
        if (flat.contains(pos_x, pos_y)) return big_key_idx + 1;

        if (big_key_idx > 0) {
            Key prev_flat = get_area_for_flat_key(big_key_idx - 2);
            if (prev_flat.contains(pos_x, pos_y)) return big_key_idx - 1;
        }

        // If not in the current or previous flat, it must be a hit in the big key
        return big_key_idx;
    }

    public Key get_area_for_key(int key_idx) {
        int x_i = key_idx / 2 * KEYS_WIDTH;
        return new Key(x_i, x_i + KEYS_WIDTH, 0, keys_height);
    }

    public Key get_area_for_flat_key(int key_idx) {
        final int octave_idx = (key_idx / 2) % 7;
        if (octave_idx == 2 || octave_idx == 6) {
            // Keys without flat get a null-area
            return new Key(0, 0, 0, 0);
        }

        final int offset = KEYS_WIDTH - (KEYS_FLAT_WIDTH / 2);
        int x_i = (key_idx / 2) * KEYS_WIDTH + offset;
        return new Key(x_i, x_i + KEYS_FLAT_WIDTH, 0, keys_flats_height);
    }

    int get_null_sound_res() {
        return R.raw.no_note;
    }

    Integer get_sound_res_for_key(int key_idx) {
        Integer KEYS_TO_SOUND[] = {
                R.raw.n01,
                R.raw.n02,
                R.raw.n03,
                R.raw.n04,
                R.raw.n05,
                null,
                R.raw.n06,
                R.raw.n07,
                R.raw.n08,
                R.raw.n09,
                R.raw.n10,
                R.raw.n11,
                R.raw.n12,
                null,
                R.raw.n13,
                R.raw.n14,
                R.raw.n15,
                R.raw.n16,
                R.raw.n17,
                null,
                R.raw.n18,
                R.raw.n19,
                R.raw.n20,
        };

        if (key_idx > KEYS_TO_SOUND.length-1 || key_idx < 0) return get_null_sound_res();
        if (KEYS_TO_SOUND[key_idx] == null) return get_null_sound_res();
        return KEYS_TO_SOUND[key_idx];
    }
}
