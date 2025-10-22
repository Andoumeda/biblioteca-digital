<template>
  <div class="settings-container">
    <div class="settings-header">
      <h2 class="settings-title">Configuración</h2>
      <p class="settings-subtitle">Administra tu cuenta y preferencias</p>
    </div>

    <div class="settings-content">
      <div class="settings-sidebar">
        <button
          v-for="section in sections"
          :key="section.id"
          @click="activeSection = section.id"
          :class="['section-btn', { active: activeSection === section.id }]"
        >
          <component :is="section.icon" />
          {{ section.name }}
        </button>
      </div>

      <div class="settings-main">
        <!-- Perfil -->
        <div v-show="activeSection === 'profile'" class="settings-section">
          <h3 class="section-title">Perfil de Usuario</h3>
          <div class="form-group">
            <label>Foto de Perfil</label>
            <div class="profile-picture-upload">
              <div class="avatar-preview">
                {{ userProfile.displayName[0]?.toUpperCase() || 'U' }}
              </div>
              <button class="btn-secondary">Cambiar Foto</button>
            </div>
          </div>

          <div class="form-group">
            <label for="displayName">Nombre para Mostrar</label>
            <input
              id="displayName"
              v-model="userProfile.displayName"
              type="text"
              class="form-input"
              placeholder="Tu nombre"
            />
          </div>

          <div class="form-group">
            <label for="username">Nombre de Usuario</label>
            <input
              id="username"
              v-model="userProfile.username"
              type="text"
              class="form-input"
              placeholder="@usuario"
              disabled
            />
            <small class="form-help">El nombre de usuario no se puede cambiar</small>
          </div>

          <div class="form-group">
            <label for="email">Correo Electrónico</label>
            <input
              id="email"
              v-model="userProfile.email"
              type="email"
              class="form-input"
              placeholder="tu@email.com"
            />
          </div>

          <div class="form-group">
            <label for="bio">Biografía</label>
            <textarea
              id="bio"
              v-model="userProfile.bio"
              class="form-textarea"
              placeholder="Cuéntanos sobre ti..."
              rows="4"
            ></textarea>
          </div>

          <button @click="saveProfile" class="btn-primary">Guardar Cambios</button>
        </div>

        <!-- Seguridad -->
        <div v-show="activeSection === 'security'" class="settings-section">
          <h3 class="section-title">Seguridad</h3>

          <div class="form-group">
            <label for="currentPassword">Contraseña Actual</label>
            <input
              id="currentPassword"
              v-model="security.currentPassword"
              type="password"
              class="form-input"
              placeholder="••••••••"
            />
          </div>

          <div class="form-group">
            <label for="newPassword">Nueva Contraseña</label>
            <input
              id="newPassword"
              v-model="security.newPassword"
              type="password"
              class="form-input"
              placeholder="••••••••"
            />
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirmar Nueva Contraseña</label>
            <input
              id="confirmPassword"
              v-model="security.confirmPassword"
              type="password"
              class="form-input"
              placeholder="••••••••"
            />
          </div>

          <button @click="changePassword" class="btn-primary">Cambiar Contraseña</button>

          <div class="section-divider"></div>

          <h4 class="subsection-title">Sesiones Activas</h4>
          <div class="sessions-list">
            <div v-for="session in activeSessions" :key="session.id" class="session-item">
              <div class="session-info">
                <div class="session-device">{{ session.device }}</div>
                <div class="session-location">{{ session.location }} • {{ session.lastActive }}</div>
              </div>
              <button @click="closeSession(session.id)" class="btn-text">Cerrar sesión</button>
            </div>
          </div>
        </div>

        <!-- Notificaciones -->
        <div v-show="activeSection === 'notifications'" class="settings-section">
          <h3 class="section-title">Notificaciones</h3>

          <div class="notification-group">
            <div class="notification-item">
              <div>
                <h4>Nuevos Libros en Publicaciones Seguidas</h4>
                <p>Recibe una notificación cuando se agreguen nuevos libros</p>
              </div>
              <label class="switch">
                <input v-model="notifications.newBooks" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="notification-item">
              <div>
                <h4>Comentarios en tus Publicaciones</h4>
                <p>Cuando alguien comenta en tus publicaciones</p>
              </div>
              <label class="switch">
                <input v-model="notifications.comments" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="notification-item">
              <div>
                <h4>Favoritos en tus Publicaciones</h4>
                <p>Cuando alguien guarda tus publicaciones como favorita</p>
              </div>
              <label class="switch">
                <input v-model="notifications.favorites" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="notification-item">
              <div>
                <h4>Recomendaciones Personalizadas</h4>
                <p>Recibe sugerencias de libros basadas en tus intereses</p>
              </div>
              <label class="switch">
                <input v-model="notifications.recommendations" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="notification-item">
              <div>
                <h4>Boletín Semanal</h4>
                <p>Resumen de las mejores publicaciones de la semana</p>
              </div>
              <label class="switch">
                <input v-model="notifications.newsletter" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>
          </div>

          <button @click="saveNotifications" class="btn-primary">Guardar Preferencias</button>
        </div>

        <!-- Privacidad -->
        <div v-show="activeSection === 'privacy'" class="settings-section">
          <h3 class="section-title">Privacidad</h3>

          <div class="privacy-group">
            <div class="privacy-item">
              <div>
                <h4>Perfil Público</h4>
                <p>Permite que otros usuarios vean tu perfil y publicaciones</p>
              </div>
              <label class="switch">
                <input v-model="privacy.publicProfile" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="privacy-item">
              <div>
                <h4>Mostrar Lista de Favoritos</h4>
                <p>Otros usuarios pueden ver tus publicaciones guardadas</p>
              </div>
              <label class="switch">
                <input v-model="privacy.showFavorites" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="privacy-item">
              <div>
                <h4>Mostrar Actividad Reciente</h4>
                <p>Muestra tus comentarios y calificaciones recientes</p>
              </div>
              <label class="switch">
                <input v-model="privacy.showActivity" type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>
          </div>

          <button @click="savePrivacy" class="btn-primary">Guardar Configuración</button>

          <div class="section-divider"></div>

          <div class="danger-zone">
            <h4 class="danger-title">Zona de Peligro</h4>
            <p>Una vez que elimines tu cuenta, no hay vuelta atrás. Por favor, ten cuidado.</p>
            <button @click="deleteAccount" class="btn-danger">Eliminar Cuenta</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, h } from 'vue';

// Iconos simples como componentes funcionales
const UserIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('path', { d: 'M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: 12, cy: 7, r: 4 })
]);

const LockIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('rect', { width: 18, height: 11, x: 3, y: 11, rx: 2, ry: 2 }),
  h('path', { d: 'M7 11V7a5 5 0 0 1 10 0v4' })
]);

const BellIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('path', { d: 'M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9' }),
  h('path', { d: 'M10.3 21a1.94 1.94 0 0 0 3.4 0' })
]);

const ShieldIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
  h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10' })
]);

export default {
  name: 'Settings',
  setup() {
    const activeSection = ref('profile');

    const sections = [
      { id: 'profile', name: 'Perfil', icon: UserIcon },
      { id: 'security', name: 'Seguridad', icon: LockIcon },
      { id: 'notifications', name: 'Notificaciones', icon: BellIcon },
      { id: 'privacy', name: 'Privacidad', icon: ShieldIcon }
    ];

    const userProfile = ref({
      displayName: 'Usuario Demo',
      username: 'usuario_demo',
      email: 'usuario@ejemplo.com',
      bio: 'Amante de los libros y el conocimiento.'
    });

    const security = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    });

    const activeSessions = ref([
      { id: 1, device: 'Chrome en Windows', location: 'Encarnación, Paraguay', lastActive: 'Activo ahora' },
      { id: 2, device: 'Firefox en Linux', location: 'General Delgado, Paraguay', lastActive: 'Hace 2 horas' }
    ]);

    const notifications = ref({
      newBooks: true,
      comments: true,
      favorites: true,
      recommendations: false,
      newsletter: true
    });

    const privacy = ref({
      publicProfile: true,
      showFavorites: true,
      showActivity: false
    });

    const saveProfile = () => {
      console.log('Guardar perfil:', userProfile.value);
      alert('Perfil actualizado correctamente');
    };

    const changePassword = () => {
      if (security.value.newPassword !== security.value.confirmPassword) {
        alert('Las contraseñas no coinciden');
        return;
      }
      console.log('Cambiar contraseña');
      alert('Contraseña actualizada correctamente');
      security.value.currentPassword = '';
      security.value.newPassword = '';
      security.value.confirmPassword = '';
    };

    const closeSession = (sessionId) => {
      console.log('Cerrar sesión:', sessionId);
      activeSessions.value = activeSessions.value.filter(s => s.id !== sessionId);
    };

    const saveNotifications = () => {
      console.log('Guardar notificaciones:', notifications.value);
      alert('Preferencias de notificaciones guardadas');
    };

    const savePrivacy = () => {
      console.log('Guardar privacidad:', privacy.value);
      alert('Configuración de privacidad guardada');
    };

    const deleteAccount = () => {
      if (confirm('¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.')) {
        console.log('Eliminar cuenta');
        alert('Cuenta eliminada');
      }
    };

    return {
      activeSection,
      sections,
      userProfile,
      security,
      activeSessions,
      notifications,
      privacy,
      saveProfile,
      changePassword,
      closeSession,
      saveNotifications,
      savePrivacy,
      deleteAccount
    };
  }
};
</script>

<style scoped>
.settings-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.settings-header {
  margin-bottom: 32px;
}

.settings-title {
  font-size: 32px;
  font-weight: bold;
  color: #1a202c;
  margin-bottom: 8px;
}

.settings-subtitle {
  color: #718096;
  font-size: 16px;
  margin: 0;
}

.settings-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 32px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  min-height: 600px;
}

.settings-sidebar {
  background: #f7fafc;
  padding: 24px 0;
  border-right: 1px solid #e2e8f0;
  min-width: 250px;
}

.section-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: transparent;
  border: none;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.section-btn:hover {
  background: white;
  color: #1a202c;
}

.section-btn.active {
  background: white;
  color: #667eea;
  border-left-color: #667eea;
}

.settings-main {
  padding: 32px 48px;
  min-width: 0;
  flex: 1;
  display: flex;
  justify-content: center;
}

.settings-section {
  width: 100%;
  max-width: 700px;
  min-width: 500px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 24px 0;
}

.subsection-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 16px 0;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 8px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1a202c;
  transition: all 0.2s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input:disabled {
  background: #f7fafc;
  cursor: not-allowed;
}

.form-help {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #718096;
}

.profile-picture-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
}

.btn-primary,
.btn-secondary,
.btn-danger,
.btn-text {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
}

.btn-secondary {
  background: white;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f7fafc;
}

.btn-danger {
  background: #f56565;
  color: white;
}

.btn-danger:hover {
  background: #e53e3e;
}

.btn-text {
  background: transparent;
  color: #667eea;
  padding: 6px 12px;
}

.btn-text:hover {
  background: #edf2f7;
}

.section-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 32px 0;
}

.sessions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.session-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
}

.session-device {
  font-size: 14px;
  font-weight: 500;
  color: #1a202c;
}

.session-location {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
}

.notification-group,
.privacy-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.notification-item,
.privacy-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
}

.notification-item h4,
.privacy-item h4 {
  font-size: 15px;
  font-weight: 500;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.notification-item p,
.privacy-item p {
  font-size: 13px;
  color: #718096;
  margin: 0;
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 26px;
  flex-shrink: 0;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #cbd5e0;
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #667eea;
}

input:checked + .slider:before {
  transform: translateX(22px);
}

.danger-zone {
  padding: 24px;
  background: #fff5f5;
  border: 1px solid #feb2b2;
  border-radius: 8px;
  margin-top: 32px;
}

.danger-title {
  font-size: 16px;
  font-weight: 600;
  color: #c53030;
  margin: 0 0 8px 0;
}

.danger-zone p {
  font-size: 14px;
  color: #742a2a;
  margin: 0 0 16px 0;
}

@media (max-width: 768px) {
  .settings-content {
    grid-template-columns: 1fr;
  }

  .settings-sidebar {
    border-right: none;
    border-bottom: 1px solid #e2e8f0;
    min-width: auto;
  }

  .settings-main {
    padding: 24px;
  }

  .settings-section {
    min-width: auto;
  }
}
</style>