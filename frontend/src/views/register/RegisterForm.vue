<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" :loading="loading" @click="handleSubmit">挂 号</el-button>
      <el-button @click="handleReset">清 空</el-button>
    </div>

    <el-form :model="form" :rules="rules" ref="regForm" label-width="92px" size="small">
      <div class="section-title">基本信息 <span class="hint"></span></div>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="必填"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="身份证号" prop="cardNumber">
            <el-input v-model="form.cardNumber" placeholder="必填，复诊自动带出" @change="fetchPatient"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="年龄">
            <el-input-number v-model="form.age" :min="0" :max="150" :controls="false" style="width:100%"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="出生日期">
            <el-date-picker v-model="form.birthdate" type="date" value-format="yyyy-MM-dd" placeholder="选填" style="width:100%"></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="16">
          <el-form-item label="家庭住址">
            <el-input v-model="form.homeAddress" placeholder="选填"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title">挂号信息</div>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="结算类别" prop="settleCategoryId">
            <el-select v-model="form.settleCategoryId" placeholder="请选择" style="width:100%">
              <el-option v-for="c in categories" :key="c.id" :label="c.settleName" :value="c.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="看诊日期" prop="visitDate">
            <el-date-picker v-model="form.visitDate" type="date" value-format="yyyy-MM-dd" style="width:100%"></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="午别">
            <el-radio-group v-model="form.noon">
              <el-radio label="上午">上午</el-radio>
              <el-radio label="下午">下午</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="挂号科室" prop="departmentId">
            <el-select v-model="form.departmentId" placeholder="请选择" @change="handleDeptChange" style="width:100%">
              <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="号别" prop="registLevelId">
            <el-select v-model="form.registLevelId" placeholder="请选择" style="width:100%">
              <el-option v-for="l in levels" :key="l.id" :label="l.registName" :value="l.id"></el-option>
            </el-select>
            <div v-if="levelTip" class="level-tip">{{ levelTip }}</div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="接诊医生" prop="employeeId">
            <el-select v-model="form.employeeId" placeholder="请选择" style="width:100%">
              <el-option v-for="e in doctors" :key="e.id" :label="e.realname" :value="e.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="应收金额">
            <el-input :value="moneyText" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="收费方式">
            <el-select v-model="form.registMethod" style="width:100%">
              <el-option v-for="m in methods" :key="m" :label="m" :value="m"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="病历本">
            <el-switch v-model="form.isBook" active-value="是" inactive-value="否" active-text="配发"></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { register, registerList } from '@/api/register'
import { departmentList, registLevelList, settleCategoryList, employeeList } from '@/api/base'

const METHODS = ['现金', '银行卡', '医保卡', '微信', '支付宝']

export default {
  name: 'RegisterForm',
  data() {
    const idCardValidator = (rule, value, cb) => {
      if (!value) {
        return cb(new Error('请输入身份证号'))
      }
      if (!/^\d{15}$|^\d{17}(\d|X|x)$/.test(value)) {
        return cb(new Error('身份证号格式不正确'))
      }
      cb()
    }
    return {
      methods: METHODS,
      loading: false,
      departments: [],
      levels: [],
      categories: [],
      employees: [],
      form: this.emptyForm(),
      rules: {
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        cardNumber: [{ validator: idCardValidator, trigger: 'blur' }],
        settleCategoryId: [{ required: true, message: '请选择结算类别', trigger: 'change' }],
        visitDate: [{ required: true, message: '请选择看诊日期', trigger: 'change' }],
        departmentId: [{ required: true, message: '请选择挂号科室', trigger: 'change' }],
        registLevelId: [{ required: true, message: '请选择号别', trigger: 'change' }],
        employeeId: [{ required: true, message: '请选择接诊医生', trigger: 'change' }]
      }
    }
  },
  computed: {
    doctors() {
      const deptId = this.form.departmentId
      return this.employees.filter(e => e.role === 'doctor' && (!deptId || e.departmentId === deptId))
    },
    selectedLevel() {
      return this.levels.find(l => l.id === this.form.registLevelId)
    },
    totalMoney() {
      let m = this.selectedLevel && this.selectedLevel.registFee ? Number(this.selectedLevel.registFee) : 0
      if (this.form.isBook === '是') m += 1
      return m
    },
    moneyText() {
      return '¥ ' + this.totalMoney.toFixed(2)
    },
    levelTip() {
      if (!this.selectedLevel) return ''
      const fee = this.selectedLevel.registFee != null ? Number(this.selectedLevel.registFee).toFixed(2) : '0.00'
      const quota = this.selectedLevel.registQuota != null ? this.selectedLevel.registQuota : '-'
      return `挂号费 ¥${fee} · 号源限额 ${quota}`
    }
  },
  created() {
    this.loadBaseData()
  },
  methods: {
    emptyForm() {
      return {
        realName: '',
        gender: '男',
        cardNumber: '',
        birthdate: '',
        age: null,
        ageType: '岁',
        homeAddress: '',
        visitDate: '',
        noon: '上午',
        departmentId: null,
        employeeId: null,
        registLevelId: null,
        settleCategoryId: null,
        isBook: '否',
        registMethod: '现金'
      }
    },
    loadBaseData() {
      Promise.all([departmentList(), registLevelList(), settleCategoryList(), employeeList()])
        .then(([depts, levels, categories, employees]) => {
          this.departments = depts
          this.levels = levels
          this.categories = categories
          this.employees = employees
        })
        .catch(() => {})
    },
    fetchPatient(cardNumber) {
      // 复诊患者：按身份证号带出最近一次挂号档案（病历号由后端复用）
      if (!cardNumber) return
      registerList({ cardNumber, page: 1, size: 1 })
        .then(rows => {
          if (rows && rows.length) {
            const r = rows[0]
            this.form.realName = r.realName
            this.form.gender = r.gender || '男'
            this.form.age = r.age
            this.form.ageType = r.ageType || '岁'
            this.form.homeAddress = r.homeAddress
            this.form.birthdate = r.birthdate ? String(r.birthdate).substring(0, 10) : ''
          }
        })
        .catch(() => {})
    },
    handleDeptChange() {
      this.form.employeeId = null
    },
    handleSubmit() {
      this.$refs.regForm.validate(valid => {
        if (!valid) return
        this.loading = true
        const data = { ...this.form }
        if (data.visitDate) data.visitDate += ' 00:00:00'
        register(data)
          .then(res => {
            this.$message.success('挂号成功，病历号：' + res.caseNumber)
            this.handleReset()
          })
          .finally(() => {
            this.loading = false
          })
      })
    },
    handleReset() {
      this.form = this.emptyForm()
      if (this.$refs.regForm) this.$refs.regForm.clearValidate()
    }
  }
}
</script>

<style scoped>
.toolbar {
  text-align: right;
}
.hint {
  font-weight: 400;
  color: #909399;
  font-size: 12px;
}
</style>
