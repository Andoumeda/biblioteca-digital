<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1>Página no encontrada</h1>
      <p>Lo sentimos, la página que estás buscando no existe.</p>
      <p class="suggestion">
        La URL que has introducido puede estar incorrecta o la página ha sido movida.
      </p>
      <div class="actions">
        <button @click="goHome" class="btn-primary">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M8 0L0 6v10h16V6L8 0zm6 14H2V7l6-4.5L14 7v7z"/>
            <path d="M6 10h4v4H6z"/>
          </svg>
          Volver al inicio
        </button>
        <button @click="goBack" class="btn-secondary">
          Regresar
        </button>
      </div>
      <div class="book-illustration">
        <svg viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="40" y="40" width="120" height="140" rx="4" fill="#e0e0e0"/>
          <rect x="45" y="45" width="110" height="130" rx="2" fill="#f5f5f5"/>
          <line x1="50" y1="60" x2="150" y2="60" stroke="#bdbdbd" stroke-width="2"/>
          <line x1="50" y1="75" x2="130" y2="75" stroke="#bdbdbd" stroke-width="2"/>
          <line x1="50" y1="90" x2="140" y2="90" stroke="#bdbdbd" stroke-width="2"/>
          <circle cx="100" cy="120" r="20" fill="#9e9e9e" opacity="0.3"/>
          <path d="M95 115 L100 120 L110 110" stroke="#616161" stroke-width="3" stroke-linecap="round"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';

export default {
  name: 'NotFound',
  setup() {
    const router = useRouter();

    const goHome = () => {
      router.push('/');
    };

    const goBack = () => {
      router.back();
    };

    return {
      goHome,
      goBack
    };
  }
};
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.not-found-content {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 100%;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 20px;
}

.not-found-content h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 600;
}

.not-found-content p {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.6;
}

.suggestion {
  font-size: 14px;
  color: #999;
  margin-bottom: 30px;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 40px;
}

.btn-primary,
.btn-secondary {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
}

.btn-secondary:hover {
  background: #e0e0e0;
  transform: translateY(-2px);
}

.book-illustration {
  margin-top: 20px;
  opacity: 0.6;
}

.book-illustration svg {
  width: 150px;
  height: 150px;
  margin: 0 auto;
}

@media (max-width: 600px) {
  .error-code {
    font-size: 80px;
  }

  .not-found-content h1 {
    font-size: 24px;
  }

  .not-found-content {
    padding: 40px 20px;
  }

  .actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }
}
</style>

