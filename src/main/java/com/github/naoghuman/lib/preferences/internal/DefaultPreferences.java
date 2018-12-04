/**
 * Copyright (C) 2018 Naoghuman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.naoghuman.lib.preferences.internal;

import com.github.naoghuman.lib.logger.core.LoggerFacade;
import java.io.File;
import java.util.Optional;

/**
 * The class {@code DefaultPreferences} allowed the access to the {@link java.util.Properties} 
 * file {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#PREFERENCES_FILE}.
 * <p>
 * The usage from the factory {@link com.github.naoghuman.lib.preferences.core.PreferencesFactory} 
 * is preferred for a simplified access to the methods in this class instead the access through the 
 * instantiation with {@code new DefaultPreferences()}.
 *
 * @since  0.6.0
 * @author Naoghuman
 * @see    com.github.naoghuman.lib.preferences.core.PreferencesFactory
 * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences
 * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#PREFERENCES_FILE
 * @see    java.util.Properties
 */
public final class DefaultPreferences {
    
    private static final String PREFERENCES_FILE          = "Preferences.properties"; // NOI18N
    private static final String SYSTEM_PROPERTY__USER_DIR = "user.dir"; // NOI18N
    
    private File file;
    private Optional<String>             optionalKey = Optional.empty();
    private DefaultPreferencesProperties properties;
    private String prefix;
    
    /**
     * Default constructor from the class {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences}.
     * 
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences
     */
    public DefaultPreferences() {
        this.initialize();
    }
    
    private void initialize() {
        LoggerFacade.getDefault().info(this.getClass(), "DefaultPreferences.initialize()"); // NOI18N
        
        final StringBuilder fileAndPath = new StringBuilder();
        fileAndPath.append(System.getProperty(SYSTEM_PROPERTY__USER_DIR));
        fileAndPath.append(File.separator);
        fileAndPath.append(PREFERENCES_FILE);
        file = new File(fileAndPath.toString());
        
        properties = new DefaultPreferencesProperties();
        DefaultPreferencesFileReader.read(file, properties);
    }
    
    /**
     * Activates the {@code application} mode.
     * <p>
     * In context from this scope a {@code key}, which allows to access or store 
     * a value into the {@link java.util.Properties}, must be <b>unique</b> in the 
     * <i>hole</i> application.<br>
     * To accomplish this behavior this method adds automatically the prefix from 
     * the class {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences}
     * to the given key.
     * 
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    java.util.Properties
     */
    public void application() {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.application()"); // NOI18N
        
        prefix = DefaultPreferences.class.getPackage().getName();
    }

    /**
     * Returnes the {@link java.lang.Boolean} {@code value} which is associated 
     * with the previouly defined {@code key} in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}.
     * <p>
     * If no value is associated with the key then the default value {@code def} 
     * will returned.<br>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  def the default value if the {@code key} doesn't exists.
     * @return the associated {@code value} or {@code def}.
     * @throws IllegalArgumentException if {@code (key == NULL)}.
     * @throws NullPointerException     if {@code (def == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Boolean
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public Boolean get(final Boolean def) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.get(Boolean)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(def);
        if (!optionalKey.isPresent()) {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        Boolean value = def;
        if (properties.containsKey(optionalKey.get())) {
            final String strValue = properties.getProperty(optionalKey.get());
            try {
                value = Boolean.valueOf(strValue);
                
                LoggerFacade.getDefault().own(this.getClass(), String.format(
                        "  Load " + optionalKey.get() + "=%b", value));// NOI18N
            } catch (NumberFormatException e) {
                LoggerFacade.getDefault().warn(this.getClass(), String.format(
                        "  Can't convert '%s' to Boolean. Use default value: %f", strValue, def)); // NOI18N
                
                value = def;
            }
        }
        else {
            LoggerFacade.getDefault().warn(this.getClass(), String.format(
                    "  Don't found key '%s' into the properties. Return default value.", optionalKey.get()));// NOI18N
        }
        
        this.resetKey();
        
        return value;
    }

    /**
     * Returnes the {@link java.lang.Double} {@code value} which is associated 
     * with the previouly defined {@code key} in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}.
     * <p>
     * If no value is associated with the key then the default value {@code def} 
     * will returned.<br>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  def the default value if the {@code key} doesn't exists.
     * @return the associated {@code value} or {@code def}.
     * @throws IllegalArgumentException if {@code (key == NULL)}.
     * @throws NullPointerException     if {@code (def == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Double
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public Double get(final Double def) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.get(Double)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(def);
        if (!optionalKey.isPresent()) {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        Double value = def;
        if (properties.containsKey(optionalKey.get())) {
            final String strValue = properties.getProperty(optionalKey.get());
            try {
                value = Double.valueOf(strValue);
                
                LoggerFacade.getDefault().own(this.getClass(), String.format(
                        "  Load " + optionalKey.get() + "=%f", value));// NOI18N
            } catch (NumberFormatException e) {
                LoggerFacade.getDefault().warn(this.getClass(), String.format(
                        "  Can't convert '%s' to Double. Use default value: %f", strValue, def)); // NOI18N
                
                value = def;
            }
        }
        else {
            LoggerFacade.getDefault().warn(this.getClass(), String.format(
                    "  Don't found key '%s' into the properties. Return default value.", optionalKey.get()));// NOI18N
        }
        
        this.resetKey();
        
        return value;
    }

    /**
     * Returnes the {@link java.lang.Integer} {@code value} which is associated 
     * with the previouly defined {@code key} in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}.
     * <p>
     * If no value is associated with the key then the default value {@code def} 
     * will returned.<br>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  def the default value if the {@code key} doesn't exists.
     * @return the associated {@code value} or {@code def}.
     * @throws IllegalArgumentException if {@code (key == NULL)}.
     * @throws NullPointerException     if {@code (def == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Integer
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public Integer get(final Integer def) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.get(Integer)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(def);
        if (!optionalKey.isPresent()) {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        Integer value = def;
        if (properties.containsKey(optionalKey.get())) {
            final String strValue = properties.getProperty(optionalKey.get());
            try {
                value = Integer.valueOf(strValue);
                
                LoggerFacade.getDefault().own(this.getClass(), String.format(
                        "  Load " + optionalKey.get() + "=%d", value));// NOI18N
            } catch (NumberFormatException e) {
                LoggerFacade.getDefault().warn(this.getClass(), String.format(
                        "  Can't convert '%s' to Integer. Use default value: %d", strValue, def)); // NOI18N
                
                value = def;
            }
        }
        else {
            LoggerFacade.getDefault().warn(this.getClass(), String.format(
                    "  Don't found key '%s' into the properties. Return default value.", optionalKey.get()));// NOI18N
        }
        
        this.resetKey();
        
        return value;
    }

    /**
     * Returnes the {@link java.lang.Long} {@code value} which is associated with 
     * the previouly defined {@code key} in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}.
     * <p>
     * If no value is associated with the key then the default value {@code def} 
     * will returned.<br>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  def the default value if the {@code key} doesn't exists.
     * @return the associated {@code value} or {@code def}.
     * @throws IllegalArgumentException if {@code (key == NULL)}.
     * @throws NullPointerException     if {@code (def == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Long
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public Long get(final Long def) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.get(Long)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(def);
        if (!optionalKey.isPresent()) {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        Long value = def;
        if (properties.containsKey(optionalKey.get())) {
            final String strValue = properties.getProperty(optionalKey.get());
            try {
                value = Long.valueOf(strValue);
                
                LoggerFacade.getDefault().own(this.getClass(), String.format(
                        "  Load " + optionalKey.get() + "=%d", value));// NOI18N
            } catch (NumberFormatException e) {
                LoggerFacade.getDefault().warn(this.getClass(), String.format(
                        "  Can't convert '%s' to Long. Use default value: %d", strValue, def)); // NOI18N
                
                value = def;
            }
        }
        else {
            LoggerFacade.getDefault().warn(this.getClass(), String.format(
                    "  Don't found key '%s' into the properties. Return default value.", optionalKey.get()));// NOI18N
        }
        
        this.resetKey();
        
        return value;
    }

    /**
     * Returnes the {@link java.lang.String} {@code value} which is associated with 
     * the previouly defined {@code key} in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}.
     * <p>
     * If no value is associated with the key then the default value {@code def} 
     * will returned.<br>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  def the default value if the {@code key} doesn't exists.
     * @return the associated {@code value} or {@code def}.
     * @throws IllegalArgumentException if {@code (def.trim() == EMPTY) || (key == NULL)}.
     * @throws NullPointerException     if {@code (def        == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNullAndNotEmpty(T)
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     * @see    java.lang.String
     */
    public String get(final String def) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.get(String)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNullAndNotEmpty(def);
        if (!optionalKey.isPresent()) {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        String value = def;
        if (properties.containsKey(optionalKey.get())) {
            value = properties.getProperty(optionalKey.get());
            
            LoggerFacade.getDefault().own(this.getClass(), String.format(
                    "  Load " + optionalKey.get() + "=%s", value));// NOI18N
        }
        else {
            LoggerFacade.getDefault().warn(this.getClass(), String.format(
                    "  Don't found key '%s' into the properties. Return default value.", optionalKey.get()));// NOI18N
        }
        
        this.resetKey();
        
        return value;
    }

    /**
     * Defines the {@code key} which {@code value} should returned.
     * <p>
     * The key can have the two scopes: {@code application} and {@code module}
     * <ul>
     * <li>Application scope means the key must <b>unique</b> in the <i>hole</i> application.<br>
     *     Executing the method {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#application()} 
     *     adds automatically the package name from this class 
     *     {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences} as prefix to the key.</li>
     * <li>Module scope means the key must <b>unique</b> in a <i>package</i> scope.<br>
     *     Executing the method {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#module(Class)} 
     *     adds automatically the package name from the given {@code clazz} as prefix to the key.</li>
     * </ul>
     * 
     * @param  key the {@code key} which {@code value} should returned.
     * @throws IllegalArgumentException if {@code (key.trim() == EMPTY)}.
     * @throws NullPointerException     if {@code (key        == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#application()
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#module(Class)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNullAndNotEmpty(T)
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public void key(final String key) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.key(String)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNullAndNotEmpty(key);
        
        optionalKey = Optional.of(key);
    }

    /**
     * Activates the {@code module} mode.
     * <p>
     * In context from this scope a {@code key}, which allows to access or store 
     * a value into the {@link java.util.Properties}, must be <b>unique</b> in a 
     * <i>package</i> scope.<br>
     * To accomplish this behavior this method adds automatically the {@code package name} 
     * from the {@code clazz} to the given key.
     * 
     * @param  clazz the package name from this {@code Class} defines the {@code module} scope.
     * @throws NullPointerException if {@code (clazz == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.NullPointerException
     * @see    java.util.Properties
     */
    public void module(final Class clazz) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.module(Class)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(clazz);
        
        prefix = clazz.getPackage().getName();
    }

    /**
     * Saves the {@link java.lang.Boolean} {@code value} to the file 
     * {@code Preferences.properties}.
     * <p>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  value the {@code value} which should be saved.
     * @throws IllegalArgumentException if {@code (key   == NULL)}.
     * @throws NullPointerException     if {@code (value == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Boolean
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public void put(final Boolean value) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.put(Boolean)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(value);
        
        if (optionalKey.isPresent()) {
            properties.put(optionalKey.get(), value);
            DefaultPreferencesFileWriter.write(properties, file);
        
            LoggerFacade.getDefault().own(this.getClass(), String.format("  Save '" + optionalKey.get() + "' to '%b'", value)); // NOI18N
        }
        else {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        this.resetKey();
    }

    /**
     * Saves the {@link java.lang.Double} {@code value} to the file 
     * {@code Preferences.properties}.
     * <p>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  value the {@code value} which should be saved.
     * @throws IllegalArgumentException if {@code (key   == NULL)}.
     * @throws NullPointerException     if {@code (value == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Double
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public void put(final Double value) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.put(Double)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(value);
        
        if (optionalKey.isPresent()) {
            properties.put(optionalKey.get(), value);
            DefaultPreferencesFileWriter.write(properties, file);
        
            LoggerFacade.getDefault().own(this.getClass(), String.format("  Save '" + optionalKey.get() + "' to '%f'", value)); // NOI18N
        }
        else {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        this.resetKey();
    }

    /**
     * Saves the {@link java.lang.Integer} {@code value} to the file 
     * {@code Preferences.properties}.
     * <p>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  value the {@code value} which should be saved.
     * @throws IllegalArgumentException if {@code (key   == NULL)}.
     * @throws NullPointerException     if {@code (value == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Integer
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public void put(final Integer value) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.put(Integer)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(value);
        
        if (optionalKey.isPresent()) {
            properties.put(optionalKey.get(), value);
            DefaultPreferencesFileWriter.write(properties, file);
        
            LoggerFacade.getDefault().own(this.getClass(), String.format("  Save '" + optionalKey.get() + "' to '%d'", value)); // NOI18N
        }
        else {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        this.resetKey();
    }

    /**
     * Saves the {@link java.lang.Long} {@code value} to the file 
     * {@code Preferences.properties}.
     * <p>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  value the {@code value} which should be saved.
     * @throws IllegalArgumentException if {@code (key   == NULL)}.
     * @throws NullPointerException     if {@code (value == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNull(java.lang.Object)
     * @see    java.lang.Long
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     */
    public void put(final Long value) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.put(Long)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNull(value);
        
        if (optionalKey.isPresent()) {
            properties.put(optionalKey.get(), value);
            DefaultPreferencesFileWriter.write(properties, file);
        
            LoggerFacade.getDefault().own(this.getClass(), String.format("  Save '" + optionalKey.get() + "' to '%d'", value)); // NOI18N
        }
        else {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        this.resetKey();
    }

    /**
     * Saves the {@link java.lang.String} {@code value} to the file 
     * {@code Preferences.properties}.
     * <p>
     * Is the associated {@code key} isn't set previously in 
     * {@link com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)}
     * an {@code IllegalArgumentException} will be thrown.
     * 
     * @param  value the {@code value} which should be saved.
     * @throws IllegalArgumentException if {@code (def.trim() == EMPTY) || (key == NULL)}.
     * @throws NullPointerException     if {@code (value == NULL)}.
     * @since  0.6.0
     * @author Naoghuman
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferences#key(String)
     * @see    com.github.naoghuman.lib.preferences.internal.DefaultPreferencesValidator#requireNonNullAndNotEmpty(T)
     * @see    java.lang.IllegalArgumentException
     * @see    java.lang.NullPointerException
     * @see    java.lang.String
     */
    public void put(final String value) {
        LoggerFacade.getDefault().debug(this.getClass(), "DefaultPreferences.put(String)"); // NOI18N
        
        DefaultPreferencesValidator.requireNonNullAndNotEmpty(value);
        
        if (optionalKey.isPresent()) {
            properties.put(optionalKey.get(), value);
            DefaultPreferencesFileWriter.write(properties, file);
        
            LoggerFacade.getDefault().own(this.getClass(), String.format("  Save '" + optionalKey.get() + "' to '%s'", value)); // NOI18N
        }
        else {
            throw new IllegalArgumentException("  No 'key' is previously defined with DefaultPreferences#key(String)."); // NOI18N
        }
        
        this.resetKey();
    }
    
    private void resetKey() {
        if (optionalKey.isPresent()) {
            optionalKey = Optional.empty();
        }
    }
    
}
