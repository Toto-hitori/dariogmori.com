<template>
  <ViewLayout>
    <template #left-col>
      <LayoutRow :justify="'end'">
        <img alt="logo" :src="'/img/game/' + props.game.id + '/logo.png'" class="image-display" style="width: 90%; rotate: -10deg;">
      </LayoutRow>
      <LayoutRow :justify="'center'" :align="'middle'" >
        <h1>{{ t('games.released') + ': ' + props.game.date}} </h1>
        <el-row align="middle">
          <h2>{{ t('games.tags') }}: </h2>
          <el-tag v-for="tag in props.game.tags"  :type="'info'">{{ tag }}</el-tag>
        </el-row>
      </LayoutRow>
      <LayoutRow :justify="'center'">
        <p>{{ t('games.' + props.game.id + '.description')}}</p>
      </LayoutRow>
      <slot name="left-col"></slot>
    </template>

    <template #center-col>
      <img alt="title" :src="getTitleURI()" style="width: 40%;" class="logo">
    </template>
    <template #right-col>
      <slot name="right-col"></slot>
    </template>
  </ViewLayout>
</template>

<script setup lang="ts">
import LayoutRow from "@/components/layout/LayoutRow.vue";
import ViewLayout from "@/components/layout/ViewLayout.vue";
import {useTranslation} from "i18next-vue";
import {onMounted, reactive} from "vue";
const props = defineProps<{
  game: {
    id: String,
    date: String,
    tags: Array<String>
  }
}>()
const { t } = useTranslation();
const config = reactive({
  theme: 'light',
  lang: 'es'
})
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  const savedLang = localStorage.getItem('lang')
  if (savedTheme) {
    config.theme = savedTheme
    document.documentElement.setAttribute('data-theme', config.theme)
  }
  if( savedLang ) {
    config.lang = savedLang
    document.documentElement.setAttribute('data-lang', config.lang)
  }
})
function getTitleURI() {
  return '/img/game/' + props.game.id + '/title-' + config.theme + '.png'
}
</script>

<style scoped>
@import '../../assets/styles/palette.css';
@import '../../assets/styles/view.css';
</style>