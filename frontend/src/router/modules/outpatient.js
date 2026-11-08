export default [{
  path: 'outpatient', name: 'Outpatient',
  component: () => import('@/views/outpatient/Outpatient.vue'),
  meta: { title: '门诊医生工作台', icon: 'el-icon-first-aid-kit', roles: ['doctor'] }
}]
