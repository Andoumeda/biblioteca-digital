<template>
  <div class="rating-system">
    <div class="stars" @mouseleave="handleMouseLeave">
      <button
        v-for="star in 5"
        :key="star"
        type="button"
        :class="['star-btn', { readonly: readonly }]"
        @click="handleStarClick(star)"
        @mouseenter="handleStarHover(star)"
        :disabled="readonly"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          :width="sizeMap[size]"
          :height="sizeMap[size]"
          viewBox="0 0 24 24"
          :fill="star <= displayRating ? 'currentColor' : 'none'"
          stroke="currentColor"
          stroke-width="2"
          :class="star <= displayRating ? 'star-filled' : 'star-empty'"
        >
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
        </svg>
      </button>
    </div>
    <span v-if="showValue" class="rating-value">
      {{ displayRating > 0 ? `${displayRating}/5` : 'Sin calificar' }}
    </span>
  </div>
</template>

<script>
import { ref, computed } from 'vue';

export default {
  name: 'RatingSystem',
  props: {
    currentRating: {
      type: Number,
      default: 0
    },
    readonly: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: 'md',
      validator: (value) => ['sm', 'md', 'lg'].includes(value)
    },
    showValue: {
      type: Boolean,
      default: false
    }
  },
  emits: ['rating-change'],
  setup(props, { emit }) {
    const hoverRating = ref(0);
    const selectedRating = ref(props.currentRating);

    const sizeMap = {
      sm: 12,
      md: 16,
      lg: 20
    };

    const displayRating = computed(() => {
      return hoverRating.value || selectedRating.value;
    });

    const handleStarClick = (rating) => {
      if (props.readonly) return;
      selectedRating.value = rating;
      emit('rating-change', rating);
    };

    const handleStarHover = (rating) => {
      if (props.readonly) return;
      hoverRating.value = rating;
    };

    const handleMouseLeave = () => {
      if (props.readonly) return;
      hoverRating.value = 0;
    };

    return {
      hoverRating,
      selectedRating,
      displayRating,
      sizeMap,
      handleStarClick,
      handleStarHover,
      handleMouseLeave
    };
  }
};
</script>

<style scoped>
.rating-system {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  display: flex;
  align-items: center;
  gap: 2px;
}

.star-btn {
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: transform 0.2s;
}

.star-btn:not(.readonly):hover {
  transform: scale(1.1);
}

.star-btn.readonly {
  cursor: default;
}

.star-filled {
  color: #f6ad55;
}

.star-empty {
  color: #e2e8f0;
}

.rating-value {
  font-size: 14px;
  color: #718096;
  margin-left: 4px;
}
</style>